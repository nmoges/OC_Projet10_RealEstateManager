package com.openclassrooms.data.repository

import android.app.Activity
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.data.*
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*
import com.openclassrooms.data.firebase.EstateDataFb
import com.openclassrooms.data.model.*
import com.openclassrooms.data.service.AutocompleteService
import java.io.File
import java.io.FileNotFoundException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Repository class interface.
 */
interface RealEstateRepositoryAccess {

    // -------------------------------- EstateDao --------------------------------------------------
    suspend fun insertEstate(estate: Estate): Long

    suspend fun updateEstate(estate: Estate)

    suspend fun getEstateWithFirebaseId(id: String): Long?

    fun getCursorEstateWithId(id: Long): Cursor

    // -------------------------------- PhotoDao ---------------------------------------------------
    suspend fun insertPhoto(photo: Photo, associatedId: Long)

    suspend fun getPhotos(id: Long): List<Photo>

    suspend fun getPhotosURIFromCloudStorage(auth: FirebaseAuth): List<Pair<Photo, Long>>

    suspend fun updatePhoto(photo: Photo, associatedId: Long)

    // -------------------------------- InteriorDao ------------------------------------------------
    suspend fun insertInterior(interior: Interior, associatedId: Long): Long

    suspend fun updateInterior(interior: Interior, associatedId: Long)

    @VisibleForTesting
    suspend fun getInteriorById(id: Long): Interior

    // -------------------------------- AgentDao ---------------------------------------------------
    suspend fun insertAgent(agent: Agent): Long

    suspend fun getAgentById(id: Long): Agent

    suspend fun getAllAgents(): List<Agent>

    suspend fun getAgentByFields(firstName: String, lastName: String): Agent?

    // -------------------------------- DatesDao ---------------------------------------------------
    suspend fun insertDates(dates: Dates, associatedId: Long): Long

    suspend fun updateDates(dates: Dates, associatedId: Long)

    suspend fun getDatesById(id: Long): Dates?

    // -------------------------------- LocationDao ------------------------------------------------
    suspend fun insertLocation(location: Location, associatedId: Long): Long

    suspend fun updateLocation(location: Location, associatedId: Long)

    @VisibleForTesting
    suspend fun getLocationById(id: Long): Location

    // -------------------------------- PointOfInterestDao -----------------------------------------
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long): Long

    suspend fun deletePointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long)

    suspend fun getPointsOfInterest(id: Long): List<PointOfInterest>

    // -------------------------------- FullEstateDao ----------------------------------------------
    fun loadAllEstates(): LiveData<List<FullEstateData>>

    fun getSearchResults(price: List<Int?>, surface: List<Int?>,
                         status: Boolean?, dates: List<String?>,
                         listPoi : MutableList<String>?, _nbFilters: Int): LiveData<List<FullEstateData>>

    // -------------------------------- Autocomplete service ---------------------------------------
    fun performAutocompleteRequest(activity: Activity)

    // -------------------------------- Cloud Storage Firebase -------------------------------------
    suspend fun sendPhotosToCloudStorage(photo: Photo, auth: FirebaseAuth): String

    // -------------------------------- Realtime Database Firebase ---------------------------------
    fun sendEstateToRealtimeDatabase(estate: Estate, dbReference: DatabaseReference)

    fun updateEstatePhotoInRealtimeDatabase(firebaseId: String, url: String, node: String,
                                            dbReference: DatabaseReference)

    fun initializeChildEventListener(dbReference: DatabaseReference, callback: (Estate) -> Unit)

    suspend fun convertListFullEstateDataToListEstate(list: List<FullEstateData>): MutableList<Estate>

    fun setLockSQLDBUpdate(status: Boolean)

}

/**
 * Repository class.
 */
class RealEstateRepository(
    private val estateDao: EstateDao,
    private val photoDao: PhotoDao,
    private val interiorDao: InteriorDao,
    private val locationDao: LocationDao,
    private val agentDao: AgentDao,
    private val datesDao: DatesDao,
    private val pointOfInterestDao: PointOfInterestDao,
    private val fullEstateDao: FullEstateDao,
    private val context: Context
):
    RealEstateRepositoryAccess {

    // -------------------------------- EstateDao --------------------------------
    /**
     * Accesses DAO Estate insertion method
     * @param estate : Estate to insert
     * @return : id of the inserted row in database
     */
    override suspend fun insertEstate(estate: Estate): Long {
        val estateData = estate.toEstateData()
        return estateDao.insertEstateData(estateData)
    }

    /**
     * Accesses DAO Estate update method
     * @param estate : Estate to update
     */
    override suspend fun updateEstate(estate: Estate) {
        val estateData = estate.toEstateData()
        estateData.idEstate = estate.id
        estateDao.updateEstateData(estateData)
    }

    /**
     * Accesses DAO Estate get method.
     * @param id : firebase id
     */
    override suspend fun getEstateWithFirebaseId(id: String): Long? {
        val estateData = estateDao.getEstateWithFirebaseId(id)
        return if (estateData != null) estateData.idEstate else null
    }

    /**
     * Accesses DAO Estate get method.
     * @param id : id
     * @param : [Cursor]
     */
    override fun getCursorEstateWithId(id: Long): Cursor = estateDao.getCursorEstateWithId(id)

    // -------------------------------- PhotoDao --------------------------------
    /**
     * Accesses DAO Photo insertion method
     * @param photo : Photo to insert
     * @param associatedId : Associated Estate id
     * @return : id of the inserted row in database
     */
    override suspend fun insertPhoto(photo: Photo, associatedId: Long) =
        photoDao.insertPhotoData(photo.toPhotoData(associatedId))

    /**
     * Accesses DAO Photo getter method
     * @param id : id of the associated estate
     * @return : list of results
     */
    override suspend fun getPhotos(id: Long): List<Photo> {
        val listPhoto = mutableListOf<Photo>()
        photoDao.getPhotos(id).forEach { listPhoto.add(it.toPhoto()) }
        return listPhoto
    }

    /**
     * Sends existing photos to Cloud Storage to get an URL in return.
     * @param auth : [FirebaseAuth] parameter
     * @param callback : callback function using updated photo
     */
    override suspend fun getPhotosURIFromCloudStorage(auth: FirebaseAuth): List<Pair<Photo, Long>> {
        // Get list of photos from Database
        val listPhotoData = mutableListOf<PhotoData>()
        photoDao.getAllPhotos().forEach { listPhotoData.add(it) }
        // Updates URI photos
        val list = mutableListOf<Pair<Photo, Long>>()
        listPhotoData.forEach { itPhotoData ->
            if (itPhotoData.uriConverted.contains("content://")) {
                val associatedId = itPhotoData.associatedId
                val photo = itPhotoData.toPhoto()
                photo.uriConverted = sendPhotosToCloudStorage(photo, auth)
                list.add(Pair(photo, associatedId))
            }
        }
        return list
    }

    /**
     * Updates an exising [PhotoData] row in SQLite database.
     * @param photo : new [Photo] to store
     * @param associatedId : associated [Estate] id
     */
    override suspend fun updatePhoto(photo: Photo, associatedId: Long) {
        val photoData = photo.toPhotoData(associatedId)
        photoData.idPhoto = photo.id
        photoDao.updatePhoto(photoData)
    }

    // -------------------------------- InteriorDao --------------------------------
    /**
     * Accesses DAO Photo insertion method
     * @param interior : Interior to insert
     * @param associatedId : Associated Estate id
     * @return : id of the inserted row in database
     */
    override suspend fun insertInterior(interior: Interior, associatedId: Long) =
        interiorDao.insertInteriorData(interior.toInteriorData(associatedId))

    /**
     * Accesses DAO Estate update method
     * @param interior : Interior to update
     * @param associatedId : Associated Estate id
     */
    override suspend fun updateInterior(interior: Interior, associatedId: Long) {
        val interiorData = interior.toInteriorData(associatedId)
        interiorData.idInterior = interior.id
        interiorDao.updateInteriorData(interiorData)
    }

    @VisibleForTesting
    override suspend fun getInteriorById(id: Long): Interior = interiorDao.getInteriorById(id).toInterior()

    // -------------------------------- AgentDao --------------------------------
    /**
     * Accesses DAO Agent insertion method
     * @param agent : Estate to insert
     * @return : id of the inserted row in database
     */
    override suspend fun insertAgent(agent: Agent): Long =
        agentDao.insertAgentData(agent.toAgentData())

    /**
     * Accesses DAO Agent getter method
     * @param id : id of the request row in database
     * @return : list of results
     */
    override suspend fun getAgentById(id: Long): Agent =
        agentDao.getAgentById(id).toAgent()

    /**
     * Accesses DAO List Agent getter method
     * @return : list of results
     */
    override suspend fun getAllAgents(): List<Agent> {
        val listAgent = mutableListOf<Agent>()
        agentDao.getAllAgents().forEach { listAgent.add(it.toAgent()) }
        return listAgent
    }

    /**
     * Accesses DAO getter method
     * @param firstName : first name of an agent
     * @param lastName : last name of an agent
     */
    override suspend fun getAgentByFields(firstName: String, lastName: String): Agent? {
        val agentData = agentDao.getAgentByFields(firstName, lastName)
        return if (agentData == null) null else agentData.toAgent()
    }


    // -------------------------------- DatesDao --------------------------------
    /**
     * Accesses DAO Dates insertion method
     * @param dates : Dates to insert
     * @param associatedId : Associated Estate id
     * @return : id of the inserted row in database
     */
    override suspend fun insertDates(dates: Dates, associatedId: Long): Long =
        datesDao.insertDateData(dates.toDatesData(associatedId))

    /**
     * Accesses DAO Dates update method
     * @param dates : Dates to update
     * @param associatedId : Associated Estate id
     */
    override suspend fun updateDates(dates: Dates, associatedId: Long) {
        val datesData = dates.toDatesData(associatedId)
        datesData.idDates = dates.id
        datesDao.updateDateData(datesData)
    }


    /**
     * Accesses DAO Photo getter method
     * @param id : id of the request row in database
     * @return : result
     */
    override suspend fun getDatesById(id: Long): Dates? = datesDao.getDatesById(id)?.toDates()

    // -------------------------------- LocationDao --------------------------------
    /**
     * Accesses DAO Location insertion method
     * @param location : Location to insert
     * @param associatedId : Associated Estate id
     * @return : id of the inserted row in database
     */
    override suspend fun insertLocation(location: Location, associatedId: Long): Long =
        locationDao.insertLocationData(location.toLocationData(associatedId))

    /**
     * Accesses DAO Location update method
     * @param location : Location to update
     * @param associatedId : Associated Estate id
     */
    override suspend fun updateLocation(location: Location, associatedId: Long) {
        val locationData = location.toLocationData(associatedId)
        locationData.idLocation = location.id
        locationDao.updateLocationData(locationData)
    }

    @VisibleForTesting
    override suspend fun getLocationById(id: Long) = locationDao.getLocationById(id).toLocation()

    // -------------------------------- PointOfInterestDao --------------------------------
    /**
     * Accesses DAO PointOfInterest insertion method
     * @param pointOfInterest : PointOfInterest to insert
     * @param associatedId : Associated Estate id
     * @return : id of the inserted row in database
     */
    override suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long): Long =
        pointOfInterestDao.insertPointOfInterestData(pointOfInterest.toPointOfInterestData(associatedId))

    /**
     * Accesses DAO PointOfInterest delete method
     * @param pointOfInterest : PointOfInterest to delete
     * @param associatedId : Associated Estate id
     */
    override suspend fun deletePointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long) {
        val pointOfInterestData = pointOfInterest.toPointOfInterestData(associatedId)
        pointOfInterestData.idPoi = pointOfInterest.id
        pointOfInterestDao.deletePointOfInterestData(pointOfInterestData)
    }


    /**
     * Accesses DAO PointOfInterest getter method
     * @param id : id of the requested row in database
     * @return : result
     */
    override suspend fun getPointsOfInterest(id: Long): List<PointOfInterest> {
        val listPointsOfInterest = mutableListOf<PointOfInterest>()
        pointOfInterestDao.getPointsOfInterest(id).forEach {
            listPointsOfInterest.add(it.toPointOfInterest())
        }
        return listPointsOfInterest
    }

    // -------------------------------- FullEstateDao --------------------------------
    /**
     * Accesses DAO load Estates method
     * @return : list of results
     */
    override fun loadAllEstates(): LiveData<List<FullEstateData>> = fullEstateDao.loadAllEstates()

    // ------------------------------- Search queries for SQLite DB --------------------------------
    companion object {
        const val TABLE_ESTATE = EstateData.TABLE_NAME
        const val TABLE_INTERIOR = InteriorData.TABLE_NAME
        const val TABLE_DATE = DatesData.TABLE_NAME
        const val TABLE_POI = PointOfInterestData.TABLE_NAME
    }

    /**
     * Performs a SQL search request to [FullEstateDao] interface
     * @param price : "Price" filter
     * @param surface : "Surface" filter
     * @param status : "Estate status" filter
     * @param dates : "Dates" filter
     * @param listPoi : "Points of interest" filter
     * @param _nbFilters : number of filters enabled
     *
     */
    override fun getSearchResults(price: List<Int?>, surface: List<Int?>,
                                  status: Boolean?, dates: List<String?>,
                                  listPoi : MutableList<String>?, _nbFilters: Int):
            LiveData<List<FullEstateData>> {
        var query = "SELECT DISTINCT $TABLE_ESTATE.* FROM $TABLE_ESTATE"
        query = addJoinClauseToSearchQuery(query, surface, dates, listPoi)
        query = addConditionsToSearchQuery(query, price, surface, status, dates, listPoi, _nbFilters)
        return fullEstateDao.getSearchResults(SimpleSQLiteQuery(query))
    }

    /**
     * Constructs SQL search request by adding JOIN clause according to the selected filters.
     * @param query : SQL query to update
     * @param surface : "Surface" filter
     * @param dates : "Dates" filter
     * @param listPoi : "Points of interest" filter
     */
    private fun addJoinClauseToSearchQuery(query: String, surface: List<Int?>,
                                           dates: List<String?>,
                                           listPoi : MutableList<String>?): String {
        var updatedQuery = query
        if(surface[0] != null && surface[1] != null)
            updatedQuery += " INNER JOIN $TABLE_INTERIOR ON $TABLE_INTERIOR.id_associated_estate = $TABLE_ESTATE.id_estate"
        if (listPoi != null)
            updatedQuery += " INNER JOIN $TABLE_POI ON $TABLE_POI.id_associated_estate = $TABLE_ESTATE.id_estate"
        if (dates[0] != null && dates[1] != null)
            updatedQuery += " INNER JOIN $TABLE_DATE ON $TABLE_DATE.id_associated_estate = $TABLE_ESTATE.id_estate"
        return updatedQuery
    }

    /**
     * Adds conditions to SQL search request according to the selected filters.
     * @param query : SQL request to update
     * @param price : "Price" filter
     * @param surface : "Surface" filter
     * @param status : "Estate status" filter
     * @param dates : "Dates" filter
     * @param listPoi : "Points of interest" filter
     * @param _nbFilters : number of selected filters
     */
    private fun addConditionsToSearchQuery(query: String, price: List<Int?>,
                                           surface: List<Int?>,
                                           status: Boolean?, dates: List<String?>,
                                           listPoi: MutableList<String>?, _nbFilters: Int): String {
        var updatedQuery = query
        updatedQuery += " WHERE"
        var nbFilters = _nbFilters
        if(price[0] != null && price[1] != null) {
            nbFilters--
            updatedQuery += " $TABLE_ESTATE.price BETWEEN ${price[0]} and ${price[1]}"
            if (nbFilters != 0) updatedQuery += " AND"
        }
        if(surface[0] != null && surface[1] != null) {
            nbFilters--
            updatedQuery += " $TABLE_INTERIOR.surface BETWEEN ${surface[0]} and ${surface[1]}"
            if (nbFilters != 0) updatedQuery += " AND"
        }
        if (listPoi != null) {
            nbFilters--
            updatedQuery += " $TABLE_POI.name IN ('"
            for (i in 0 until listPoi.size) {
                updatedQuery += if (i < listPoi.size-1) "${listPoi[i]}', '" else "${listPoi[i]}')"
            }
            if (nbFilters != 0) updatedQuery += " AND"
        }
        if(status != null) {
            nbFilters--
            updatedQuery += " $TABLE_ESTATE.status = ${if(status) 1 else 0}"
            if (nbFilters != 0) updatedQuery += " AND"
        }
        if (dates[0] != null && dates[1] != null) {
            nbFilters--
            updatedQuery += " $TABLE_DATE.entry_date >= '${dates[0]}' AND $TABLE_DATE.entry_date <= '${dates[1]}'"
            if (nbFilters != 0) updatedQuery += " AND"
        }
        return updatedQuery
    }

    // ------------------------------- Autocomplete Service ----------------------------------------
    /**
     * Performs an autocomplete request.
     * @param activity : parent activity
     */
    override fun performAutocompleteRequest(activity: Activity) =
        AutocompleteService.performAutocompleteRequest(activity)


    // ------------------------------- Cloud Storage access ----------------------------------------
    /**
     * Sends existing photos to Cloud Storage
     * @param photo : photo to store in Cloud Storage
     * @param auth : [FirebaseAuth] parameter
     */

    override suspend fun sendPhotosToCloudStorage(photo: Photo, auth: FirebaseAuth): String {
        return suspendCoroutine<String> { result ->
            try {
                val storageReference = FirebaseStorage.getInstance().reference
                val userID = auth.currentUser?.uid
                val ref = storageReference.child("images/users/$userID/${photo.name}.jpg")
                ref.putFile(Uri.parse(photo.uriConverted)).addOnSuccessListener { itTask ->
                    itTask.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        result.resume(url.toString())
                    }
                }.addOnFailureListener{ exception -> result.resumeWith(Result.failure(exception)) }

            } catch (exception: FileNotFoundException) {
                exception.printStackTrace()
            }
        }
    }

    // ------------------------------- Realtime Database access  -----------------------------------

    /**
     * Lock parameter used for filtering Realtime database updates. Only updates from others users must
     * be stored in SQLite user database.
     */
    var lockSQLiteDBUpdate: Boolean = false

    /**
     * Setter method
     * @param status : value to set
     */
    override fun setLockSQLDBUpdate(status: Boolean) {
        lockSQLiteDBUpdate = status
    }

    /**
     * Sends [Estate] to Realtime database.
     * @param estate : [Estate] to send
     * @param dbReference : Database reference
     */
    override fun sendEstateToRealtimeDatabase(estate: Estate, dbReference: DatabaseReference) {
        dbReference.child(estate.firebaseId).setValue(estate.toEstateDataFb())
    }

    override fun updateEstatePhotoInRealtimeDatabase(firebaseId: String, url: String, node: String,
                                                     dbReference: DatabaseReference) {
       dbReference.child(firebaseId).child("listPhotoDataFb")
                                           .child(node).child("uriConverted").setValue(url)
    }

    /**
     * Initializes Realtime database child event listener to catch updates.
     * @param dbReference : Database reference
     * @param callback : callback to return new database update
     */
    override fun initializeChildEventListener(dbReference: DatabaseReference, callback: (Estate) -> Unit) {
        val childListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (!lockSQLiteDBUpdate) {
                    val child = snapshot.getValue(EstateDataFb::class.java)
                    child?.let {
                        if (it.firebaseId != previousChildName) callback(it.toEstate())
                    }
                }
                else lockSQLiteDBUpdate = false
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (!lockSQLiteDBUpdate) {
                    val child = snapshot.getValue(EstateDataFb::class.java)
                    child?.let { callback(it.toEstate()) }
                }
                else lockSQLiteDBUpdate = false
            }

            override fun onChildRemoved(snapshot: DataSnapshot) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onCancelled(error: DatabaseError) { }
        }
        dbReference.addChildEventListener(childListener)
    }

    /**
     * Converts [FullEstateData] into [Estate]
     */
    override suspend fun convertListFullEstateDataToListEstate(list: List<FullEstateData>):
            MutableList<Estate>{
        val listConverted: MutableList<Estate> = mutableListOf()

        list.forEach { it ->
            it.estateData?.let { itEstate ->
                val interior = it.interiorData?.toInterior()
                val listPhoto: MutableList<Photo> = mutableListOf()
                it.listPhotosData?.forEach { listPhoto.add(it.toPhoto()) }
                val listPointOfInterest: MutableList<PointOfInterest> = mutableListOf()
                it.listPointOfInterestData?.forEach {
                    listPointOfInterest.add(it.toPointOfInterest())
                }
                val agent = getAgentById(itEstate.idAgent)
                val dates = getDatesById(itEstate.idEstate)
                val location = it.locationData?.toLocation()
                if (interior != null && location != null && dates != null) {
                    val estate = it.estateData.toEstate(interior, listPhoto, agent, dates,
                        location, listPointOfInterest)
                    listConverted.add(estate)
                }
            }

        }
        return listConverted
    }
}