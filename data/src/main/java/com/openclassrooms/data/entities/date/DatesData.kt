package com.openclassrooms.data.entities.date

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_dates" table.
 * @param idDates : id of a date
 * @param entryDateData : [EntryDateData] object, defining "entry date" columns
 * @param saleDateData : [SaleDateData] object, defining "sale date" columns
 * @param associatedId : associated estate id
 */
@Entity(tableName = "table_dates")
data class DatesData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_dates")
    var idDates: Long = 0,

    @Embedded
    val entryDateData: EntryDateData,

    @Embedded
    val saleDateData: SaleDateData,

    @ColumnInfo(name = "id_associated_estate")
    var associatedId: Long
)