package com.openclassrooms.data.dao

import android.database.Cursor
import androidx.room.*
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

    // ---------------------------------- TEST -------------------------
    // TODO() : Add suspend
    @Query("SELECT * FROM table_estates WHERE id_estate = :id") // TODO() : Add jointure
    fun getEstateWithId(id: Long): Cursor
}