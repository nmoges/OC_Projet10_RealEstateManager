 package com.openclassrooms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.openclassrooms.data.entities.InteriorData

/**
 * Data access object interface to [com.openclassrooms.data.database.RealEstateManagerDatabase]
 * table_interiors.
 */
@Dao
interface InteriorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInteriorData(interiorData: InteriorData)

    @Update
    suspend fun updateInteriorData(interiorData: InteriorData)
}