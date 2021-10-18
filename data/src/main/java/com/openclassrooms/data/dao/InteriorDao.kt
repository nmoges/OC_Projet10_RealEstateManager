 package com.openclassrooms.data.dao

import androidx.annotation.VisibleForTesting
import androidx.room.*
import com.openclassrooms.data.entities.InteriorData
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Data access object interface to [RealEstateManagerDatabase] table_interiors.
 */
@Dao
interface InteriorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInteriorData(interiorData: InteriorData): Long

    @Update
    suspend fun updateInteriorData(interiorData: InteriorData)

    @VisibleForTesting
    @Query("SELECT * FROM table_interiors WHERE id_interior = :id")
    suspend fun getInteriorById(id: Long): InteriorData
}