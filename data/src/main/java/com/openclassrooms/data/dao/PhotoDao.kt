package com.openclassrooms.data.dao

import androidx.room.*
import com.openclassrooms.data.entities.PhotoData
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Data access object interface to [RealEstateManagerDatabase] table_photos.
 */
@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotoData(photoData: PhotoData)

    @Query("SELECT * FROM table_photos WHERE id_associated_estate = :id")
    suspend fun getPhotos(id: Long): List<PhotoData>

    @Query("SELECT * FROM table_photos")
    suspend fun getAllPhotos() : List<PhotoData>

    @Update
    suspend fun updatePhoto(photoData: PhotoData)
}