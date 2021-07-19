package com.openclassrooms.realestatemanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_interiors")
data class InteriorData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_interior")
    val idInterior: Int,

    @ColumnInfo(name = "number_rooms")
    var numberRooms: Int,

    @ColumnInfo(name = "number_bathrooms")
    var numberBathrooms: Int,

    @ColumnInfo(name = "number_bedrooms")
    var numberBedrooms: Int,

    var surface: Int,

    @ColumnInfo(name = "id_associated_estate")
    val idAssociatedEstate: Int
)