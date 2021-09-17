package com.openclassrooms.data.repository

import android.app.Activity
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.data.*
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.DatesData
import com.openclassrooms.data.model.*
import com.openclassrooms.data.service.AutocompleteService

/**
 * Repository class interface.
 */
interface RealEstateRepositoryAccess {

    // -------------------------------- EstateDao --------------------------------
    suspend fun insertEstate(estate: Estate): Long

    suspend fun updateEstate(estate: Estate)

    // -------------------------------- PhotoDao --------------------------------
    suspend fun insertPhoto(photo: Photo, associatedId: Long)

    suspend fun getPhotos(id: Long): List<Photo>

    // -------------------------------- InteriorDao --------------------------------
    suspend fun insertInterior(interior: Interior, associatedId: Long)

    suspend fun updateInterior(interior: Interior, associatedId: Long)

    // -------------------------------- AgentDao --------------------------------
    suspend fun insertAgent(agent: Agent): Long

    suspend fun getAgentById(id: Long): Agent

    suspend fun getAllAgents(): List<Agent>

    // -------------------------------- DatesDao --------------------------------
    suspend fun insertDates(dates: Dates, associatedId: Long): Long

    suspend fun updateDates(dates: Dates, associatedId: Long)

    suspend fun getDatesById(id: Long): Dates

    // -------------------------------- LocationDao --------------------------------
    suspend fun insertLocation(location: Location, associatedId: Long): Long

    suspend fun updateLocation(location: Location, associatedId: Long)

    // -------------------------------- PointOfInterestDao --------------------------------
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long)

    suspend fun deletePointOfInterest(pointOfInterest: PointOfInterest, associatedId: Long)

    suspend fun getPointsOfInterest(id: Long): List<PointOfInterest>

    // -------------------------------- FullEstateDao --------------------------------
    suspend fun loadAllEstates(): List<Estate>

    fun getSearchResults(price: List<Int?>, surface: List<Int?>,
                         status: Boolean?, dates: List<String?>,
                         listPoi : MutableList<String>?, _nbFilters: Int): LiveData<List<FullEstateData>>

    // -------------------------------- Autocomplete service --------------------------------
    fun performAutocompleteRequest(activity: Activity)

    // --------------------- TEST ---------------------------------
    fun getEstateWithId(id: Long): Cursor

    suspend fun convertListFullEstateDataToListEstate(list: List<FullEstateData>): MutableList<Estate>
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
    override suspend fun insertEstate(estate: Estate): Long =
        estateDao.insertEstateData(estate.toEstateData())


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
    override suspend fun loadAllEstates(): List<Estate> {
        val list = fullEstateDao.loadAllEstates()
        val listConverted: MutableList<Estate> = mutableListOf()
        list.forEach { it ->
            val interior = it.interiorData.toInterior()
            val listPhoto: MutableList<Photo> = mutableListOf()
            it.listPhotosData.forEach { listPhoto.add(it.toPhoto()) }
            val listPointOfInterest: MutableList<PointOfInterest> = mutableListOf()
            it.listPointOfInterestData.forEach { listPointOfInterest.add(it.toPointOfInterest()) }
            val agent = getAgentById(it.estateData.idAgent)
            val dates = getDatesById(it.estateData.idEstate)
            val location = it.locationData.toLocation()
            val estate = it.estateData.toEstate(interior, listPhoto, agent, dates,
                                                location, listPointOfInterest)
            listConverted.add(estate)
        }
        return listConverted
    }

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

    // Autocomplete Service
    override fun performAutocompleteRequest(activity: Activity) {
        AutocompleteService.performAutocompleteRequest(activity)
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