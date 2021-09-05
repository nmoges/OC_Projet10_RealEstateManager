package com.openclassrooms.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.data.entities.FullEstateData

@Dao
interface FullEstateDao {

    @Transaction
    @Query("SELECT * FROM table_estates")
    suspend fun loadAllEstates() : List<FullEstateData>

    @Transaction
    @RawQuery
    fun getSearchResults(query: SimpleSQLiteQuery): LiveData<List<FullEstateData>>
}