package com.openclassrooms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.openclassrooms.data.entities.EstateData

/**
 * Data access object interface to [com.openclassrooms.data.database.RealEstateManagerDatabase]
 * table_estates.
 */
@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEstateData(estateData: EstateData): Long

    @Update
    suspend fun updateEstateData(estateData: EstateData)
}