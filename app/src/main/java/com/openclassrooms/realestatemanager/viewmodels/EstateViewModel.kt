package com.openclassrooms.realestatemanager.viewmodels

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.openclassrooms.data.entities.PointOfInterestData
import com.openclassrooms.data.model.*
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.utils.poi.POIComparator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EstateViewModel @Inject constructor(
    private val repositoryAccess: RealEstateRepositoryAccess): ViewModel() {

    /** Temporary value for type of operation (estate/update creation) */
    var typeOperation:  Boolean = false

    /** Contains temporary [Estate] object */
    lateinit var estate: Estate

    /** Contains temporary number of photo added by user */
    var numberPhotosAdded: Int = 0

    /** Contains temporary uri value */
    var photoUriEstate: String = ""

    /** Contains a temporary list of points of interest */
    val listPOI: MutableList<String> = mutableListOf()

    /** Contains temporary agent id selected in dialog*/
    var idAgentSelected: Long = 1

    /** Temporary value storing the error display status for sliders */
    var errorSlidersStatus: Boolean = false

    /** Temporary value storing the position in the list of agents */
    var nameAgentSelected: String = ""

    init {
        Firebase.database.setPersistenceEnabled(true)
        resetEstate()
    }

    /**
     * Creates a new [Estate].
     */
    private fun createNewEstate(): Estate = Estate(
        id = 1, type = "", price = 0, description = "", status = false, selected = false,
        location = Location(id = 1, address = "", district = "",
            latitude = 0.0, longitude = 0.0),
        interior = Interior(id= 1, numberRooms = 5, numberBathrooms = 1,
            numberBedrooms = 1, surface = 50),
        agent = Agent(id = 1, firstName = "", lastName = ""),
        dates = Dates(id = 1, dateEntry = "", dateSale = "")
    )

    /**
     * Reset [estate] and [listPOI] values.
     */
    fun resetEstate() {
        estate = createNewEstate()
        listPOI.clear()
    }

    /**
     * Access "initializeChildEventListener()" method from [repositoryAccess].
     */
    fun initializeChildEventListener(dbReference: DatabaseReference, callback: () -> (Unit)) {
        repositoryAccess.initializeChildEventListener(dbReference) { itEstate ->
            viewModelScope.launch {
                val oldEstateId = repositoryAccess.getEstateWithFirebaseId(itEstate.firebaseId)
                val agent = repositoryAccess.getAgentByFields(itEstate.agent.firstName,
                                                              itEstate.agent.lastName)
                if (agent == null) {
                    val idNewAgent = repositoryAccess
                                            .insertAgent(Agent(firstName = itEstate.agent.firstName,
                                                               lastName = itEstate.agent.lastName))
                    itEstate.agent.id = idNewAgent
                }
                else itEstate.agent.id = agent.id
                if (oldEstateId == null) // Insert
                    insertEstateInDatabase(itEstate)
                else { // Update
                    oldEstateId.let { id ->
                        itEstate.id = id
                        itEstate.location.id = id
                        itEstate.interior.id = id
                        itEstate.dates.id = id
                    }
                    updateEstateInDatabase(itEstate)
                }
                // End loading estates
                callback()
            }
        }
    }

    /**
     * Initializes [estate] with [selectedEstate] values (Update estate).
     * @param selectedEstate : Estate to modify
     */
    fun initializeWithSelectedEstateValues(selectedEstate: Estate) {
        estate = selectedEstate
        estate.listPointOfInterest.forEach { listPOI.add(it.name) }
    }

    /**
     * Removes photos added to an [Estate] if user cancelled its modifications.
     */
    fun removePhotosIfEstateCreationCancelled() {
        while (numberPhotosAdded > 0) {
            estate.listPhoto.removeLast()
            numberPhotosAdded--
        }
    }

    /**
     * Creates a new [Photo] object for an [Estate]
     */
    fun createNewPhoto(namePhoto: String): Photo? {
        val uri = photoUriEstate
        return if (uri.isNotEmpty()) { Photo(0, uri, namePhoto) } else null
    }

    /**
     * Updates location value.
     * @param place : Place returned from an autocomplete request
     * @param context : Context
     */
    fun updateLocationSelectedEstate(place : Place, context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        place.latLng?.let {
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            if (addresses.isNotEmpty()) {
                val district = addresses.first().subLocality ?: addresses.first().locality
                estate.location.apply {
                    this.latitude = it.latitude
                    this.longitude = it.longitude
                    this.address = place.address ?: ""
                    this.district = district
                }
            }
        }
    }

    /**
     * Adds date to selected estate.
     * @param type : type of date to add (entry date or sale date)
     */
    fun updateDateEstate(type: Boolean) {
        if (!type) estate.dates.dateEntry = Utils.getDate()
        else estate.dates.dateSale = Utils.getDate()
    }

    /**
     * Returns a LiveData containing the new estate/updated estate to send to database.
     * @return : LiveData<Estate>
     */
    fun getNewEstate(context: Context?, auth: FirebaseAuth): LiveData<Estate> {
        val newEstate = MutableLiveData<Estate>()
        viewModelScope.launch {
            // Update agent
            val agent = repositoryAccess.getAgentById(idAgentSelected)
            estate.agent.apply {
                this.id = agent.id
                this.firstName = agent.firstName
                this.lastName = agent.lastName
            }
            // Add entry date
            updateDateEstate(false)
            // Add list Points of interest
            estate.listPointOfInterest.clear()
            for (i in 0 until listPOI.size) {
                estate.listPointOfInterest.add(PointOfInterest(i.toLong(), listPOI[i]))
            }
            listPOI.clear()
            // Send photos to cloud Storage
            if (numberPhotosAdded > 0) {
                context?.let {
                    if (Utils.isInternetAvailable(context)) {
                        for (i in estate.listPhoto.size-numberPhotosAdded until estate.listPhoto.size) {
                                val url = repositoryAccess.sendPhotosToCloudStorage(estate.listPhoto[i],
                                                                                    auth)
                                estate.listPhoto[i].uriConverted = url
                                if (i == estate.listPhoto.size-1 && estate.listPhoto[i].uriConverted == url) {
                                    numberPhotosAdded = 0
                                    newEstate.postValue(estate)
                                }
                        }
                    }
                    else {
                        numberPhotosAdded = 0
                        newEstate.postValue(estate)
                    }
                }
            }
            else newEstate.postValue(estate)
        }
        return newEstate
    }

    // -------------------- Data insertion --------------------
    /**
     * Access insert estate method from repository interface.
     * @param estate : estate data to store in table_photos
     */
    private suspend fun insertEstateInDatabase(estate: Estate): Long {
        val id = repositoryAccess.insertEstate(estate)
        insertInteriorInDatabase(estate.interior, id)
        insertDatesInDatabase(estate.dates, id)
        insertLocationInDatabase(estate.location, id)
        estate.listPhoto.forEach { insertPhotoInDatabase(it, id) }
        estate.listPointOfInterest.forEach { insertPointOfInterestInDatabase(it, id) }
        return id
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
    fun updateSQLiteDatabase(dbReference: DatabaseReference,
                             typeUpdate: Boolean,
                             estate: Estate,
                             callback: () -> Unit) {
        viewModelScope.launch {
            if (!typeUpdate) {
                val id = insertEstateInDatabase(estate)
                estate.id = id
                repositoryAccess.setLockSQLDBUpdate(true)
                repositoryAccess.sendEstateToRealtimeDatabase(estate, dbReference)
            }
            else {
                updateEstateInDatabase(estate)
                repositoryAccess.setLockSQLDBUpdate(true)
                repositoryAccess.sendEstateToRealtimeDatabase(estate, dbReference)
            }
            callback()
        }
    }

    /**
     * Updates table_estates from database.
     * @param estate : updated estate
     */
    private suspend fun updateEstateInDatabase(estate: Estate) {
        repositoryAccess.updateEstate(estate)                 // Update table_estates
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
}