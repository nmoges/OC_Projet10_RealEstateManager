package com.openclassrooms.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.openclassrooms.data.entities.FullEstateData

@Dao
interface FullEstateDao {

    @Transaction
    @Query("SELECT * FROM table_estates")
    fun loadAllEstates() : LiveData<List<FullEstateData>>
}