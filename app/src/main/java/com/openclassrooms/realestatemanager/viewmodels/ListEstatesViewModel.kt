package com.openclassrooms.realestatemanager.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.openclassrooms.data.entities.PointOfInterestData
import com.openclassrooms.data.model.*
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.service.DummyListAgentGenerator
import com.openclassrooms.realestatemanager.utils.poi.POIComparator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * View Model class containing the estates values.
 */
@HiltViewModel
class ListEstatesViewModel @Inject constructor(
    private val repositoryAccess: RealEstateRepositoryAccess): ViewModel() {

    /** Contains list of existing [Estate] objects.  */
    private val _listEstates: MutableLiveData<List<Estate>> = MutableLiveData()
    val listEstates: LiveData<List<Estate>>
        get() = _listEstates

    /** Contains a temporary photo uri converted value. */
    private val _photoUriEstate: MutableLiveData<String> = MutableLiveData()
    private val photoUriEstate: LiveData<String>
        get() = _photoUriEstate

    /** Contains a list of agents. */
    private val _listAgents: MutableLiveData<List<Agent>> = MutableLiveData()
    val listAgents: LiveData<List<Agent>>
        get() = _listAgents

    /** Contains a temporary list of points of interest */
    val listPOI: MutableList<String> = mutableListOf()

    /** Contains selected [Estate]. */
    var selectedEstate: Estate? = null

    init {
        insertDummyListAgentInDb()
        restoreData()
    }

    // -------------------- Estate update --------------------
    /**
     * Creates a new [Estate].
     */
    fun createNewEstate() {
        selectedEstate =
            Estate(
                id = 1, type = "", price = 0, description = "", status = false, selected = false,
                location = Location(id = 1, address = "", district = "",
                                    latitude = 0.0, longitude = 0.0),
                interior = Interior(id= 1, numberRooms = 5, numberBathrooms = 1,
                                    numberBedrooms = 1, surface = 50),
                agent = Agent(id = 1, firstName = "", lastName = ""),
                dates = Dates(id = 1, dateEntry = "", dateSale = "")
            )
    }

    /**
     * Set a selected [Estate] by user (click on item) to [selectedEstate]
     */
    fun setSelectedEstate(position: Int) { selectedEstate = listEstates.value?.get(position) }

    /**
     * Update [selectedEstate] field with [Agent] values.
     * @param id : selected id agent
     * @param updateEstate : defines type of operation (creation or update estate)
     */
    fun updateAgentSelectedEstate(id: Int, updateEstate: Boolean) {
        viewModelScope.launch {
            val agent = repositoryAccess.getAgentById(id.toLong())
            selectedEstate?.agent.apply {
                this?.id = agent.id
                this?.firstName = agent.firstName
                this?.lastName = agent.lastName
            }
            updateDatabase(updateEstate)
        }
    }

    /**
     * Updates [selectedEstate] field with [Interior] values.
     * @param numberRooms : number of rooms
     * @param numberBathrooms : number of bathrooms
     * @param numberBedrooms : number of bedrooms
     * @param surface : surface estate
     */
    fun updateInteriorSelectedEstate(numberRooms: Int, numberBathrooms: Int,
                                     numberBedrooms: Int, surface: Int ) {
        selectedEstate?.interior.apply {
            this?.numberRooms = numberRooms
            this?.numberBedrooms = numberBedrooms
            this?.numberBathrooms = numberBathrooms
            this?.surface = surface
        }
    }

    /**
     * Updates [selectedEstate] field values.
     * @param type : type of estate
     * @param description : description of the estate
     * @param price : price of the estate
     */
    fun updateSelectedEstate(type: String, description: String, price: Int) {
        selectedEstate?.apply {
            this.type = type
            this.description = description
            this.price = price
        }
    }

    /**
     * Adds date to selected estate.
     * @param type : type of date to add (entry date or sale date)
     */
    fun updateDateSelectedEstate(type: Boolean) {
        if (!type) selectedEstate?.dates?.dateEntry = Utils.getDate()
        else selectedEstate?.dates?.dateSale = Utils.getDate()
    }

    /**
     * Updates [selectedEstate] location field value.
     * @param place : Place returned from an autocomplete request
     * @param context : Context
     */
    fun updateLocationSelectedEstate(place : Place, context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        place.latLng?.let {
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            if (addresses.isNotEmpty()) {
                val district = addresses.first().subLocality ?: addresses.first().locality

                selectedEstate?.apply {
                    location.latitude = it.latitude
                    location.longitude = it.longitude
                    location.address = place.address ?: ""
                    location.district = district
                }
            }
        }
    }

    /**
     * Updates [selectedEstate] point of interest field value.
     */
    fun updatePointOfInterestSelectedEstate() {
        selectedEstate?.apply {
            listPointOfInterest.apply {
                clear()
                for (i in 0 until listPOI.size) {
                    add(PointOfInterest((i+1).toLong(), listPOI[i]))
                }
                listPOI.clear()
            }
        }
    }
    // -------------------- Handle photo --------------------
    /**
     * Creates a new [Photo] object for an [Estate]
     */
    fun createNewPhoto(namePhoto: String): Photo? {
        val uri = photoUriEstate.value
        return if (uri != null && uri.isNotEmpty()) { Photo(uri, namePhoto) } else null
    }

    /**
     * Adds new uri value.
     * @param photoUri : converted uri
     */
    fun updatePhotoUri(photoUri: String) = _photoUriEstate.postValue(photoUri)

    /**
     * Clears temporary uri values.
     */
    fun clearTempPhotoUri() = _photoUriEstate.postValue(null)

    /**
     * Removes photos added to an [Estate] if user cancelled its modifications.
     * @param numberPhotosAdded : Number of photos added since the beginning of the modifications.
     */
    fun removePhotosIfEstateCreationCancelled(numberPhotosAdded: Int) {
        var indice = numberPhotosAdded
        while (indice > 0) {
            selectedEstate?.listPhoto?.removeLast()
            indice--
        }
    }

    // -------------------- Data restoration --------------------
    /**
     * Restores data from database.
     */
    private fun restoreData() {
        // TODO() : listEState -> Mediator Live Data
        viewModelScope.launch { _listEstates.postValue(repositoryAccess.loadAllEstates()) }
    }

    /**
     * Restores the list of existing agents in database.
     */
    fun restoreListAgents() =
        viewModelScope.launch { _listAgents.postValue(repositoryAccess.getAllAgents()) }

    // -------------------- Data insertion --------------------
    /**
     * Access insert estate method from repository interface.
     * @param estate : estate data to store in table_photos
     */
    private suspend fun insertEstateInDatabase(estate: Estate) {
            val id = repositoryAccess.insertEstate(estate)
            insertInteriorInDatabase(estate.interior, id)
            insertDatesInDatabase(estate.dates, id)
            insertLocationInDatabase(estate.location, id)
            estate.listPhoto.forEach { insertPhotoInDatabase(it, id) }
            estate.listPointOfInterest.forEach { insertPointOfInterestInDatabase(it, id) }
    }

    /**
     * Access insert photo method from repository interface.
     ** @param photo : Photo data to store in table_photos
     * @param associatedId : associated estate id
     */
    private fun insertPhotoInDatabase(photo: Photo, associatedId: Long) =
        viewModelScope.launch { repositoryAccess.insertPhoto(photo, associatedId) }

    /**
     * Access insert interior method from repository interface.
     * @param interior : interior data to store in table_interiors
     * @param associatedId : associated estate id
     */
    private fun insertInteriorInDatabase(interior: Interior, associatedId: Long) =
        viewModelScope.launch { repositoryAccess.insertInterior(interior, associatedId) }

    /**
     * Access insert dates method from repository interface.
     * @param dates : dates data to store in table_dates
     * @param associatedId : associated estate id
     */
    private fun insertDatesInDatabase(dates: Dates, associatedId: Long) =
        viewModelScope.launch { repositoryAccess.insertDates(dates, associatedId) }

    /**
     * Access insert location method from repository interface.
     * @param location : location to store in table_locations
     * @param associatedId : associated estate id
     */
    private fun insertLocationInDatabase(location: Location, associatedId: Long) =
        viewModelScope.launch { repositoryAccess.insertLocation(location, associatedId) }

    /**
     * Access insert agent method from repository interface.
     * @param agent : agent to store in table_agents
     */
    fun insertAgentInDatabase(agent: Agent) =
        viewModelScope.launch { repositoryAccess.insertAgent(agent) }

    /**
     * Access insert point of interest method from repository interface.
     * @param pointOfInterest : point of interest to store in table_poi
     * @param associatedId : associated estate id
     */
    private fun insertPointOfInterestInDatabase(pointOfInterest: PointOfInterest, associatedId: Long) =
        viewModelScope.launch { repositoryAccess.insertPointOfInterest(pointOfInterest, associatedId) }

    // -------------------- Data update --------------------
    /**
     * Determines if database operation is an insertion or an update.
     * @param typeUpdate : type of operation in database
     */
    fun updateDatabase(typeUpdate: Boolean) {
        val estate = selectedEstate ?: return
        viewModelScope.launch {
            if (!typeUpdate) insertEstateInDatabase(estate)
            else updateEstateInDatabase(estate)
            restoreData()
        }
    }

    /**
     * Updates table_estates from database.
     * @param estate : updated estate
     */
    private suspend fun updateEstateInDatabase(estate: Estate) {
            repositoryAccess.updateEstate(estate)             // Update table_estates
            updateInteriorInDatabase(estate.interior, estate.id)  // Update table_interiors
            updatePhotosInDatabase(estate)                        // Update table_photos
            updateDatesInDatabase(estate.dates, estate.id)        // Update table_dates
            updateLocationInDatabase(estate.location, estate.id)  // Update table_locations
            updatePointsOfInterestInDatabase(estate)              // Update table_poi
    }

    /**
     * Updates table_interiors from database.
     * @param interior : updated interior
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateInteriorInDatabase(interior: Interior, idAssociatedEstate: Long) =
        viewModelScope.launch { repositoryAccess.updateInterior(interior, idAssociatedEstate) }

    /**
     * Updates table_dates from database.
     * @param dates : updated dates
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateDatesInDatabase(dates: Dates, idAssociatedEstate: Long) =
        viewModelScope.launch { repositoryAccess.updateDates(dates, idAssociatedEstate) }

    /**
     * Updates table_locations from database.
     * @param location : updated location
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateLocationInDatabase(location: Location, idAssociatedEstate: Long) =
        viewModelScope.launch { repositoryAccess.updateLocation(location, idAssociatedEstate) }

    /**
     * Updates table_photos from database.
     * @param estate : estate containing updated photos
     */
    private suspend fun updatePhotosInDatabase(estate: Estate) {
        val listPhotosInDb = repositoryAccess.getPhotos(estate.id)
        if (estate.listPhoto.size > listPhotosInDb.size) { // At least one new photo added
            for (i in listPhotosInDb.size until estate.listPhoto.size) {
                insertPhotoInDatabase(estate.listPhoto[i], estate.id)
            }
        }
    }

    /**
     * Updates table_poi from database.
     * @param estate : estate containing updated point of interest
     */
    private suspend fun updatePointsOfInterestInDatabase(estate: Estate) {
        val listPOIInDb: MutableList<PointOfInterest> = mutableListOf()
        repositoryAccess.getPointsOfInterest(estate.id).forEach { listPOIInDb.add(it) }
        when {
            estate.listPointOfInterest.size > listPOIInDb.size -> {
                for (i in 0 until estate.listPointOfInterest.size) {
                    if (!POIComparator.containsPOI(estate.listPointOfInterest[i], listPOIInDb)) {
                        insertPointOfInterestInDatabase(estate.listPointOfInterest[i], estate.id)
                    }
                }
            }
            estate.listPointOfInterest.size < listPOIInDb.size -> {
                for (i in 0 until listPOIInDb.size) {
                    if (!POIComparator.containsPOI(listPOIInDb[i], estate.listPointOfInterest)) {
                        deletePointOfInterestFromDatabase(listPOIInDb[i], estate.id)
                    }
                }
            }
            estate.listPointOfInterest.size == listPOIInDb.size -> {
                for (i in 0 until listPOIInDb.size) {
                    if (!POIComparator.containsPOI(listPOIInDb[i], estate.listPointOfInterest)) {
                        deletePointOfInterestFromDatabase(listPOIInDb[i], estate.id)
                    }
                }
                for (i in 0 until estate.listPointOfInterest.size) {
                    if (!POIComparator.containsPOI(estate.listPointOfInterest[i], listPOIInDb)) {
                        insertPointOfInterestInDatabase(estate.listPointOfInterest[i], estate.id)
                    }
                }
            }
        }

    }
    // -------------------- Data removal --------------------
    /**
     * Removes a row in table_poi associated corresponding to a given [PointOfInterestData]
     * @param pointOfInterest : data to remove
     * @param associatedId : associated estate id
     */
    private suspend fun deletePointOfInterestFromDatabase(pointOfInterest: PointOfInterest,
    associatedId: Long) {
        repositoryAccess.deletePointOfInterest(pointOfInterest, associatedId)
    }

    // -------------------- Autocomplete --------------------
    /**
     * Performs an autocomplete request.
     * @param activity : Main activity
     */
    fun performAutocompleteRequest(activity: Activity) {
        repositoryAccess.performAutocompleteRequest(activity)
    }

    /**
     * Test method to insert fake date in table_agents from database.
     */
    private fun insertDummyListAgentInDb() {
        viewModelScope.launch {
            val nbAgents = repositoryAccess.getAllAgents().size
            if (nbAgents == 0) {
                DummyListAgentGenerator.listAgents.forEach {
                    repositoryAccess.insertAgent(it)
                }
            }
        }
    }
}

