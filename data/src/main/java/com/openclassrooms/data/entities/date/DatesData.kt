package com.openclassrooms.data.entities.date

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase
import java.util.*

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_dates" table.
 * @param idDates : id of a date
 * @param associatedId : associated estate id
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