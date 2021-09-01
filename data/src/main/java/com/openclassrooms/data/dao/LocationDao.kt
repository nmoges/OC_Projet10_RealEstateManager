package com.openclassrooms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
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
}