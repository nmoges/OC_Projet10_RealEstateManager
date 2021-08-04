package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_locations")
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_location")
    var idLocation: Long = 0,

    var latitude: Double,

    var longitude: Double,

    var address: String,

    var district: String,

    @ColumnInfo(name = "id_associated_estate")
    var associatedId: Long
)