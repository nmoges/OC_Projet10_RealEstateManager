package com.openclassrooms.data.repository

import android.app.Activity
import android.database.Cursor
import androidx.lifecycle.LiveData
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.date.DatesData
import com.openclassrooms.data.service.AutocompleteService
import com.openclassrooms.data.service.RetrofitBuilder
import retrofit2.Retrofit

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

}