package com.openclassrooms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.data.entities.PhotoData

/**
 * Data access object interface to [com.openclassrooms.data.database.RealEstateManagerDatabase]
 * table_photos.
 */
@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotoData(photoData: PhotoData)

    @Query("SELECT * FROM table_photos WHERE id_associated_estate = :id")
    suspend fun getPhotos(id: Long): List<PhotoData>
}