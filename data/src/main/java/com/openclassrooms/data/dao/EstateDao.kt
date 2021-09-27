package com.openclassrooms.data.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.data.entities.EstateData
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Data access object interface to [RealEstateManagerDatabase] table_estates.
 */
@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEstateData(estateData: EstateData): Long

    @Update
    suspend fun updateEstateData(estateData: EstateData)

    // ---------------------------------- TEST -------------------------
    // TODO() : Add suspend
    @Query("SELECT * FROM table_estates WHERE id_estate = :id")
    fun getEstateWithId(id: Long): Cursor

    @Query("SELECT * FROM table_estates WHERE id_firebase = :id")
    suspend fun getEstateWithFirebaseId(id: String): EstateData?
}