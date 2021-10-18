package com.openclassrooms.data.dao

import androidx.annotation.VisibleForTesting
import androidx.room.*
import com.openclassrooms.data.database.RealEstateManagerDatabase
import com.openclassrooms.data.entities.LocationData

/**
 * Data access object interface to [RealEstateManagerDatabase] table_locations.
 */
@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocationData(locationData: LocationData): Long

    @Update
    suspend fun updateLocationData(locationData: LocationData)

    @VisibleForTesting
    @Query("SELECT * FROM table_locations WHERE id_location = :id")
    suspend fun getLocationById(id: Long): LocationData
}