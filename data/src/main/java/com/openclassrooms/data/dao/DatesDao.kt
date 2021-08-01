package com.openclassrooms.data.dao

import androidx.room.*
import com.openclassrooms.data.entities.date.DatesData

@Dao
interface DatesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDateData(datesData: DatesData): Long

    @Update
    suspend fun updateDateData(datesData: DatesData)

    @Query("SELECT * FROM table_dates WHERE id_dates = :id")
    suspend fun getDatesById(id: Long): DatesData
}