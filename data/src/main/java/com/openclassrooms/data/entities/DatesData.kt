package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

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