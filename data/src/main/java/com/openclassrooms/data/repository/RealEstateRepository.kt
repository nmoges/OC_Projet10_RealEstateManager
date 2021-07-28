package com.openclassrooms.data.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.data.dao.EstateDao
import com.openclassrooms.data.dao.EstateWithPhotosAndInteriorDao
import com.openclassrooms.data.dao.InteriorDao
import com.openclassrooms.data.dao.PhotoDao
import com.openclassrooms.data.entities.EstateData
import com.openclassrooms.data.entities.EstateDataWithPhotosAndInterior
import com.openclassrooms.data.entities.InteriorData
import com.openclassrooms.data.entities.PhotoData

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

    // EstateWithPhotosAndInteriorDao
    fun loadAllEstates(): LiveData<List<EstateDataWithPhotosAndInterior>>
}

/**
 * Repository class.
 */
class RealEstateRepository(
    private val estateDao: EstateDao,
    private val photoDao: PhotoDao,
    private val interiorDao: InteriorDao,
    private val estateWithPhotosAndInteriorDao: EstateWithPhotosAndInteriorDao): RealEstateRepositoryAccess {

    // EstateDao

    override suspend fun insertEstate(estateData: EstateData): Long = estateDao.insertEstateData(estateData)

    override suspend fun updateEstate(estateData: EstateData) = estateDao.updateEstateData(estateData)

    // PhotoDao
    override suspend fun insertPhoto(photoData: PhotoData) = photoDao.insertPhotoData(photoData)

    override suspend fun getPhotos(id: Long): List<PhotoData> = photoDao.getPhotos(id)

    // InteriorDao
    override suspend fun insertInterior(interiorData: InteriorData) =
                                                        interiorDao.insertInteriorData(interiorData)

    override suspend fun updateInterior(interiorData: InteriorData) = interiorDao.updateInteriorData(interiorData)


    // EstateWithPhotosAndInteriorDao
    override fun loadAllEstates(): LiveData<List<EstateDataWithPhotosAndInterior>> =
                                                     estateWithPhotosAndInteriorDao.loadAllEstates()
}