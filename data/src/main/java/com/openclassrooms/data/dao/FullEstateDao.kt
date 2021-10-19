package com.openclassrooms.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.data.entities.FullEstateData

/**
 * Data access object interface to access tables_estates and linking rows with others tables.
 */
@Dao
interface FullEstateDao {

    @Transaction
    @Query("SELECT * FROM table_estates")
    fun loadAllEstates() : LiveData<List<FullEstateData>>

    @Transaction
    @RawQuery
    fun getSearchResults(query: SimpleSQLiteQuery): LiveData<List<FullEstateData>>
}