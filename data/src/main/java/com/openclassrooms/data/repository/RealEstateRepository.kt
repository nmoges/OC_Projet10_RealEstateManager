package com.openclassrooms.data.repository

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.data.*
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*
import com.openclassrooms.data.model.*
import com.openclassrooms.data.service.AutocompleteService

/**
 * Repository class interface.
 */
interface RealEstateRepositoryAccess {

    // -------------------------------- EstateDao --------------------------------------------------
    suspend fun insertEstate(estate: Estate, type: Boolean): Long

    suspend fun updateEstate(estate: Estate)

    // -------------------------------- PhotoDao ---------------------------------------------------
    suspend fun insertPhoto(photo: Photo, associatedId: Long)

    suspend fun getPhotos(id: Long): List<Photo>

    // -------------------------------- InteriorDao ------------------------------------------------
    suspend fun insertInterior(interior: Interior, associatedId: Long)

    suspend fun updateInterior(interior: Interior, associatedId: Long)

    // -------------------------------- AgentDao ---------------------------------------------------
    suspend fun insertAgent(agent: Agent): Long

    suspend fun getAgentById(id: Long): Agent

    suspend fun getAllAgents(): List<Agent>

    // -------------------------------- DatesDao ---------------------------------------------------
    suspend fun insertDates(dates: Dates, associatedId: Long): Long

    suspend fun updateDates(dates: Dates, associatedId: Long)

    suspend fun getDatesById(id: Long): Dates

    // -------------------------------- LocationDao ------------------------------------------------
    suspend fun insertLocation(location: Location, associatedId: Long): Long

    suspend fun updateLocation(location: Location, associatedId: Long)

    // -------------------------------- PointOfInterestDao -----------------------------------------
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long)

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
    fun sendPhotosToCloudStorage(photo: Photo, auth: FirebaseAuth, callbackURL: (String) -> Unit)

    // -------------------------------- Realtime Database Firebase ---------------------------------
    fun sendEstateToRealtimeDatabase(estate: Estate, dbReference: DatabaseReference)

    fun initializeChildEventListener(dbReference: DatabaseReference, callbackList: (Estate, Boolean) -> Unit)

    // --------------------- TEST ---------------------------------
    fun getEstateWithId(id: Long): Cursor

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
    private val fullEstateDao: FullEstateDao):
    RealEstateRepositoryAccess {

    // -------------------------------- EstateDao --------------------------------
    /**
     * Accesses DAO Estate insertion method
     * @param estate : Estate to insert
     * @return : id of the inserted row in database
     */
    override suspend fun insertEstate(estate: Estate, type: Boolean): Long {
        val estateData = estate.toEstateData()
        if (type) estateData.idEstate = estate.id // From Realtime database, Id already exists
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
     * @param id : id of the request row in database
     * @return : list of results
     */
    override suspend fun getPhotos(id: Long): List<Photo> {
        val listPhoto = mutableListOf<Photo>()
        photoDao.getPhotos(id).forEach { listPhoto.add(it.toPhoto()) }
        return listPhoto
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
    override suspend fun getDatesById(id: Long): Dates = datesDao.getDatesById(id).toDates()

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


    // -------------------------------- PointOfInterestDao --------------------------------
    /**
     * Accesses DAO PointOfInterest insertion method
     * @param pointOfInterest : PointOfInterest to insert
     * @param associatedId : Associated Estate id
     * @return : id of the inserted row in database
     */
    override suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long) =
        pointOfInterestDao.insertPointOfInterestData(pointOfInterest.toPointOfInterestData(associatedId))

    /**
     * Accesses DAO PointOfInterest delete method
     * @param pointOfInterest : PointOfInterest to delete
     * @param associatedId : Associated Estate id
     */
    override suspend fun deletePointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long) =
        pointOfInterestDao.deletePointOfInterestData(pointOfInterest.toPointOfInterestData(associatedId))

    /**
     * Accesses DAO PointOfInterest getter method
     * @param id : id of the request row in database
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
    override fun loadAllEstates(): LiveData<List<FullEstateData>> {
        return fullEstateDao.loadAllEstates()
    }


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
    //TODO() : A déplacer dans une data classe
    override fun getSearchResults(price: List<Int?>, surface: List<Int?>,
                                  status: Boolean?, dates: List<String?>,
                                  listPoi : MutableList<String>?, _nbFilters: Int): LiveData<List<FullEstateData>> {
        var query = "SELECT DISTINCT $TABLE_ESTATE.* FROM $TABLE_ESTATE"
        query = addJoinClauseToSearchQuery(query, surface, dates, listPoi)
        query = addConditionsToSearchQuery(query, price, surface, status, dates, listPoi, _nbFilters)
        Log.i("QUERY", "$query")
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
    override fun performAutocompleteRequest(activity: Activity) {
        AutocompleteService.performAutocompleteRequest(activity)
    }

    // ------------------------------- Cloud Storage access ----------------------------------------
    override fun sendPhotosToCloudStorage(photo: Photo, auth: FirebaseAuth, callbackURL: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val userID = auth.currentUser?.uid
        val ref = storageReference.child("images/users/$userID/${photo.name}.jpg")
        ref.putFile(Uri.parse(photo.uriConverted)).addOnSuccessListener { itTask ->
            itTask.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                callbackURL(it.toString())
            }
        }.addOnFailureListener{ it.printStackTrace() }
    }

    // ------------------------------- Realtime Database access  -----------------------------------

    var lockSQLiteDBUpdate: Boolean = false

    override fun setLockSQLDBUpdate(status: Boolean) {
        lockSQLiteDBUpdate = status
    }

    override fun sendEstateToRealtimeDatabase(estate: Estate, dbReference: DatabaseReference) {

        dbReference.child(estate.id.toString()).setValue(estate)
    }

    override fun initializeChildEventListener(dbReference: DatabaseReference, callbackList: (Estate, Boolean) -> Unit) {

        val childListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (!lockSQLiteDBUpdate) {
                    val child = snapshot.getValue(Estate::class.java)
                    child?.let {   callbackList(it, false) }
                }
                else lockSQLiteDBUpdate = false
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (!lockSQLiteDBUpdate) {
                    val child = snapshot.getValue(Estate::class.java)
                    child?.let {   callbackList(it, true) }
                }
                else lockSQLiteDBUpdate = false
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        dbReference.addChildEventListener(childListener)
    }



    // --------------------- TEST ---------------------------------
    override fun getEstateWithId(id: Long): Cursor = estateDao.getEstateWithId(id)

    override suspend fun convertListFullEstateDataToListEstate(list: List<FullEstateData>): MutableList<Estate>{
        val listConverted: MutableList<Estate> = mutableListOf()
        list.forEach { it ->
            val interior = it.interiorData.toInterior()
            val listPhoto: MutableList<Photo> = mutableListOf()
            it.listPhotosData.forEach {
                listPhoto.add(it.toPhoto())
            }
            val listPointOfInterest: MutableList<PointOfInterest> = mutableListOf()
            it.listPointOfInterestData.forEach {
                listPointOfInterest.add(it.toPointOfInterest())
            }
            val agent = getAgentById(it.estateData.idAgent)
            val dates = getDatesById(it.estateData.idEstate)
            val location = it.locationData.toLocation()
            val estate = it.estateData.toEstate(interior, listPhoto, agent, dates,
                location, listPointOfInterest)
            listConverted.add(estate)
        }
        return listConverted
    }
}