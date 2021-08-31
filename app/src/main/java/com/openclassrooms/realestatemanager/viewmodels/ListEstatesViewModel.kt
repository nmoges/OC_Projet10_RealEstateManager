package com.openclassrooms.realestatemanager.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.openclassrooms.data.entities.FullEstateData
import com.openclassrooms.data.entities.PointOfInterestData
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.model.*
import com.openclassrooms.realestatemanager.model.Dates
import com.openclassrooms.realestatemanager.service.DummyListAgentGenerator
import com.openclassrooms.realestatemanager.utils.*
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
    val repositoryAccess: RealEstateRepositoryAccess): ViewModel() {

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

    /** Contains selected [Estate]. */
    var selectedEstate: Estate? = null

    init { restoreListAgents() }

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
                                    numberBedrooms = 1, surface = 200),
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
            val agentData = repositoryAccess.getAgentById(id.toLong())
            val agent = agentData.toAgent()
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
            val district = addresses.first().subLocality ?: addresses.first().locality

            selectedEstate?.apply {
                location.latitude = it.latitude
                location.longitude = it.longitude
                location.address = place.address ?: ""
                location.district = district
            }
        }
    }

    /**
     * Updates [selectedEstate] point of interest field value.
     * @param list : new point of interest list
     */
    fun updatePointOfInterestSelectedEstate(list: MutableList<String>) {
        selectedEstate?.apply {
            listPointOfInterest.apply {
                clear()
                for (i in 0 until list.size) {
                    add(PointOfInterest((i+1).toLong(), list[i]))
                }
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
     * @param list : list of data
     */
    fun restoreData(list: List<FullEstateData>) {
        viewModelScope.launch {
            _listEstates.postValue(Converters.convertListFullEstateDataToListEstate(list,
                                                                                  repositoryAccess))
        }
    }

    /**
     * Restores the list of existing agents in database.
     */
    private fun restoreListAgents() {
        viewModelScope.launch {
            val listAgentData = repositoryAccess.getAllAgents()
            val listAgents: MutableList<Agent> = mutableListOf()
            listAgentData.forEach {
                listAgents.add(it.toAgent())
            }
            _listAgents.postValue(listAgents)
        }
    }

    // -------------------- Data insertion --------------------
    /**
     * Access insert estate method from repository interface.
     * @param estate : estate data to store in table_photos
     */
    private fun insertEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = estate.toEstateData()
            val id = repositoryAccess.insertEstate(estateData)
            insertInteriorInDatabase(estate.interior, id)
            insertDatesInDatabase(estate.dates, id)
            insertLocationInDatabase(estate.location, id)
            estate.listPhoto.forEach {
                insertPhotoInDatabase(it, id)
            }
            estate.listPointOfInterest.forEach {
                insertPointOfInterestInDatabase(it, id)
            }
        }
    }

    /**
     * Access insert photo method from repository interface.
     ** @param photo : Photo data to store in table_photos
     * @param associatedId : associated estate id
     */
    private fun insertPhotoInDatabase(photo: Photo, associatedId: Long) {
        viewModelScope.launch {
            val photoData = photo.toPhotoData(associatedId)
            repositoryAccess.insertPhoto(photoData)
        }
    }

    /**
     * Access insert interior method from repository interface.
     * @param interior : interior data to store in table_interiors
     * @param associatedId : associated estate id
     */
    private fun insertInteriorInDatabase(interior: Interior, associatedId: Long) {
        viewModelScope.launch {
            val interiorData = interior.toInteriorData(associatedId)
            repositoryAccess.insertInterior(interiorData)
        }
    }

    /**
     * Access insert dates method from repository interface.
     * @param dates : dates data to store in table_dates
     * @param associatedId : associated estate id
     */
    private fun insertDatesInDatabase(dates: Dates, associatedId: Long) {
        viewModelScope.launch {
            val datesData = dates.toDatesData(associatedId)
            repositoryAccess.insertDates(datesData)
        }
    }

    /**
     * Access insert location method from repository interface.
     * @param location : location to store in table_locations
     * @param associatedId : associated estate id
     */
    private fun insertLocationInDatabase(location: Location, associatedId: Long) {
        viewModelScope.launch {
            val locationData = location.toLocationData(associatedId)
            repositoryAccess.insertLocation(locationData)
        }
    }

    /**
     * Access insert agent method from repository interface.
     * @param agent : agent to store in table_agents
     */
    fun insertAgentInDatabase(agent: Agent) {
        viewModelScope.launch {
            val agentData = agent.toAgentData()
            repositoryAccess.insertAgent(agentData)
        }
    }

    /**
     * Access insert point of interest method from repository interface.
     * @param pointOfInterest : point of interest to store in table_poi
     * @param associatedId : associated estate id
     */
    private fun insertPointOfInterestInDatabase(pointOfInterest: PointOfInterest, associatedId: Long) {
        viewModelScope.launch {
            val pointOfInterestData = pointOfInterest.toPointOfInterestData(associatedId)
            repositoryAccess.insertPointOfInterest(pointOfInterestData)
        }
    }


    // -------------------- Data update --------------------
    /**
     * Determines if database operation is an insertion or an update.
     * @param typeUpdate : type of operation in database
     */
    fun updateDatabase(typeUpdate: Boolean) {
        val estate = selectedEstate ?: return
        if (!typeUpdate) insertEstateInDatabase(estate)
        else updateEstateInDatabase(estate)
    }

    /**
     * Updates table_estates from database.
     * @param estate : updated estate
     */
    private fun updateEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = estate.toEstateData()
            estateData.idEstate = estate.id
            repositoryAccess.updateEstate(estateData)             // Update table_estates
            updateInteriorInDatabase(estate.interior, estate.id)  // Update table_interiors
            updatePhotosInDatabase(estate)                        // Update table_photos
            updateDatesInDatabase(estate.dates, estate.id)        // Update table_dates
            updateLocationInDatabase(estate.location, estate.id)  // Update table_locations
            updatePointsOfInterestInDatabase(estate)              // Update table_poi
        }
    }

    /**
     * Updates table_interiors from database.
     * @param interior : updated interior
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateInteriorInDatabase(interior: Interior, idAssociatedEstate: Long) {
        viewModelScope.launch {
            val interiorData = interior.toInteriorData(idAssociatedEstate)
            interiorData.idInterior = idAssociatedEstate
            repositoryAccess.updateInterior(interiorData)
        }
    }

    /**
     * Updates table_dates from database.
     * @param dates : updated dates
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateDatesInDatabase(dates: Dates, idAssociatedEstate: Long) {
        viewModelScope.launch {
            val datesData = dates.toDatesData(idAssociatedEstate)
            datesData.idDates = idAssociatedEstate
            repositoryAccess.updateDates(datesData)
        }
    }

    /**
     * Updates table_locations from database.
     * @param location : updated location
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateLocationInDatabase(location: Location, idAssociatedEstate: Long) {
        viewModelScope.launch {
            val locationData = location.toLocationData(idAssociatedEstate)
            locationData.idLocation = idAssociatedEstate
            repositoryAccess.updateLocation(locationData)
        }
    }

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
        repositoryAccess.getPointsOfInterest(estate.id).forEach {
            listPOIInDb.add(it.toPointOfInterest())
        }
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
                        val pointOfInterestData = listPOIInDb[i].toPointOfInterestData(estate.id)
                        pointOfInterestData.idPoi = listPOIInDb[i].id
                        deletePointOfInterestFromDatabase(pointOfInterestData)
                    }
                }
            }
        }

    }
    // -------------------- Data removal --------------------
    /**
     * Removes a row in table_poi associated corresponding to a given [PointOfInterestData]
     * @param pointOfInterestData : data to remove
     */
    private suspend fun deletePointOfInterestFromDatabase(pointOfInterestData: PointOfInterestData) {
        repositoryAccess.deletePointOfInterest(pointOfInterestData)
    }

    // -------------------- Autocomplete --------------------
    /**
     * Performs an autocomplete request.
     * @param activity : Main activity
     */
    fun performAutocompleteRequest(activity: Activity) {
        repositoryAccess.performAutocompleteRequest(activity)
    }


    // TODO() : test function to remove later : used to inject dummy agent data
    fun test() {
        viewModelScope.launch {
            DummyListAgentGenerator.listAgents.forEach {
                repositoryAccess.insertAgent(it.toAgentData())
            }
            restoreListAgents()
        }
    }

}

