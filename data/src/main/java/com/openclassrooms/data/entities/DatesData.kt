package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_dates" table.
 * @param idDates : id of a date
 * @param associatedId : associated estate id
 * @param dateEntry : entry date of an estate
 * @param dateSale : sale date of an estate
 */
@Entity(tableName = "table_dates")
data class DatesData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_dates")
    var idDates: Long = 0,

    @ColumnInfo(name ="entry_date")
    var dateEntry : String,

    @ColumnInfo(name ="sale_date")
    var dateSale : String,

    @ColumnInfo(name = "id_associated_estate")
    var associatedId: Long
) {
    companion object {
        const val TABLE_NAME = "table_dates"
    }
}