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

/**
 * Repository class.
 */
class RealEstateRepository(
    private val estateDao: EstateDao,
    private val photoDao: PhotoDao,
    private val interiorDao: InteriorDao,
    private val estateWithPhotosAndInteriorDao: EstateWithPhotosAndInteriorDao) {

    // EstateDao
    suspend fun insertEstate(estateData: EstateData): Long = estateDao.insertEstateData(estateData)

    suspend fun updateEstate(estateData: EstateData) = estateDao.updateEstateData(estateData)

    // PhotoDao
    suspend fun insertPhoto(photoData: PhotoData) = photoDao.insertPhotoData(photoData)

    suspend fun getPhotos(id: Long): List<PhotoData> = photoDao.getPhotos(id)

    // InteriorDao
    suspend fun insertInterior(interiorData: InteriorData) =
                                                        interiorDao.insertInteriorData(interiorData)

    suspend fun updateInterior(interiorData: InteriorData) = interiorDao.updateInteriorData(interiorData)


    // EstateWithPhotosAndInteriorDao
    fun loadAllEstates(): LiveData<List<EstateDataWithPhotosAndInterior>> =
                                                     estateWithPhotosAndInteriorDao.loadAllEstates()
}