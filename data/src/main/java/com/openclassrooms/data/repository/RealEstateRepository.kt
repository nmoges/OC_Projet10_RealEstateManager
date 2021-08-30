package com.openclassrooms.data.repository

import android.app.Activity
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.date.DatesData
import com.openclassrooms.data.service.AutocompleteService
import com.openclassrooms.data.service.RetrofitBuilder
import retrofit2.Retrofit
import kotlin.math.max

/**
 * Repository class interface.
 */
interface RealEstateRepositoryAccess {

    // EstateDao
    suspend fun insertEstate(estateData: EstateData): Long

    suspend fun updateEstate(estateData: EstateData)

    // PhotoDao
    suspend fun insertPhoto(photoData: PhotoData)

    suspend fun getPhotos(id: Long): List<PhotoData>

    // InteriorDao
    suspend fun insertInterior(interiorData: InteriorData)

    suspend fun updateInterior(interiorData: InteriorData)

    // AgentDao
    suspend fun insertAgent(agentData: AgentData): Long

    suspend fun getAgentById(id: Long): AgentData

    suspend fun getAllAgents(): List<AgentData>

    // DatesDao
    suspend fun insertDates(datesData: DatesData): Long

    suspend fun updateDates(datesData: DatesData)

    suspend fun getDatesById(id: Long): DatesData

    // LocationDao
    suspend fun insertLocation(locationData: LocationData): Long

    suspend fun updateLocation(locationData: LocationData)

    // PointOfInterestDao
    suspend fun insertPointOfInterest(pointOfInterestData: PointOfInterestData)

    suspend fun deletePointOfInterest(pointOfInterestData: PointOfInterestData)

    suspend fun getPointsOfInterest(id: Long): List<PointOfInterestData>

    // EstateWithPhotosAndInteriorDao
    fun loadAllEstates(): LiveData<List<FullEstateData>>

    // Autocomplete service
    fun performAutocompleteRequest(activity: Activity)

    // Retrofit
    fun getRetrofit(): Retrofit

    // --------------------- TEST ---------------------------------
    fun getEstateWithId(id: Long): Cursor

    fun getSearchResults(price: ArrayList<Int?>, surface: ArrayList<Int?>,
                         status: Boolean?, listPoi : MutableList<String>?, _nbFilters: Int): LiveData<List<FullEstateData>>
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

    // EstateDao

    override suspend fun insertEstate(estateData: EstateData): Long =
        estateDao.insertEstateData(estateData)

    override suspend fun updateEstate(estateData: EstateData) =
        estateDao.updateEstateData(estateData)

    // PhotoDao
    override suspend fun insertPhoto(photoData: PhotoData) =
        photoDao.insertPhotoData(photoData)

    override suspend fun getPhotos(id: Long): List<PhotoData> =
        photoDao.getPhotos(id)

    // InteriorDao
    override suspend fun insertInterior(interiorData: InteriorData) =
        interiorDao.insertInteriorData(interiorData)

    override suspend fun updateInterior(interiorData: InteriorData) {
        interiorDao.updateInteriorData(interiorData)
    }

    // AgentDao
    override suspend fun insertAgent(agentData: AgentData): Long =
        agentDao.insertAgentData(agentData)

    override suspend fun getAgentById(id: Long): AgentData =
        agentDao.getAgentById(id)

    override suspend fun getAllAgents(): List<AgentData> =
        agentDao.getAllAgents()

    // DatesDao
    override suspend fun insertDates(datesData: DatesData): Long = datesDao.insertDateData(datesData)

    override suspend fun updateDates(datesData: DatesData) = datesDao.updateDateData(datesData)

    override suspend fun getDatesById(id: Long): DatesData = datesDao.getDatesById(id)

    // LocationDao
    override suspend fun insertLocation(locationData: LocationData): Long =
                                                        locationDao.insertLocationData(locationData)

    override suspend fun updateLocation(locationData: LocationData) =
                                                        locationDao.updateLocationData(locationData)

    // PointOfInterestDao
    override suspend fun insertPointOfInterest(pointOfInterestData: PointOfInterestData) =
        pointOfInterestDao.insertPointOfInterestData(pointOfInterestData)


    override suspend fun deletePointOfInterest(pointOfInterestData: PointOfInterestData) =
        pointOfInterestDao.deletePointOfInterestData(pointOfInterestData)

    override suspend fun getPointsOfInterest(id: Long): List<PointOfInterestData>
    = pointOfInterestDao.getPointsOfInterest(id)

    // EstateWithPhotosAndInteriorDao
    override fun loadAllEstates(): LiveData<List<FullEstateData>> =
                                                     fullEstateDao.loadAllEstates()

    // Autocomplete Service
    override fun performAutocompleteRequest(activity: Activity) {
        AutocompleteService.performAutocompleteRequest(activity)
    }

    // Retrofit
    override fun getRetrofit(): Retrofit = RetrofitBuilder.retrofit

    // --------------------- TEST ---------------------------------
    override fun getEstateWithId(id: Long): Cursor = estateDao.getEstateWithId(id)


    companion object {
        const val TABLE_ESTATE = EstateData.TABLE_NAME
        const val TABLE_INTERIOR = InteriorData.TABLE_NAME
        const val TABLE_LOCATION = LocationData.TABLE_NAME
        const val TABLE_DATE = DatesData.TABLE_NAME
        const val TABLE_POI = PointOfInterestData.TABLE_NAME
    }

    override fun getSearchResults(price: ArrayList<Int?>, surface: ArrayList<Int?>,
                                  status: Boolean?, listPoi : MutableList<String>?, _nbFilters: Int): LiveData<List<FullEstateData>> {
        var query = "SELECT DISTINCT $TABLE_ESTATE.* FROM $TABLE_ESTATE"
        query = addJoinClauseToSearchQuery(query, surface[0], surface[1], listPoi)
        query = addConditionsToSearchQuery(query, price[0], price[1], surface[0], surface[1], status, listPoi, _nbFilters)

        Log.i("SQL_REQUEST", query)
        return fullEstateDao.getSearchResults(SimpleSQLiteQuery(query))
    }

    private fun addJoinClauseToSearchQuery(query: String, minSurface: Int?, maxSurface: Int?, listPoi : MutableList<String>?): String {
        var updatedQuery = query
        if(minSurface != null && maxSurface != null)
            updatedQuery += " INNER JOIN $TABLE_INTERIOR ON $TABLE_INTERIOR.id_associated_estate = $TABLE_ESTATE.id_estate"
        if (listPoi != null)
            updatedQuery += " INNER JOIN $TABLE_POI ON $TABLE_POI.id_associated_estate = $TABLE_ESTATE.id_estate"
        // TODO: Add TABLE_DATE
        Log.i("SQL_REQUEST", "Join : $updatedQuery")
        return updatedQuery
    }

    private fun addConditionsToSearchQuery(query: String, minPrice: Int?, maxPrice: Int?,
                                           minSurface: Int?, maxSurface: Int?,
                                           status: Boolean?, listPoi: MutableList<String>?,
                                           _nbFilters: Int): String {
        var updatedQuery = query
        updatedQuery += " WHERE"
        var nbFilters = _nbFilters
        if(minPrice != null && maxPrice != null) {
            nbFilters--
            updatedQuery += " $TABLE_ESTATE.price BETWEEN $minPrice and $maxPrice"
            if (nbFilters != 0) updatedQuery += " AND"
        }
        if(minSurface != null && maxSurface != null) {
            nbFilters--
            updatedQuery += " $TABLE_INTERIOR.surface BETWEEN $minSurface and $maxSurface"
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
        Log.i("SQL_REQUEST", "Condition : $updatedQuery")
        return updatedQuery
    }
}