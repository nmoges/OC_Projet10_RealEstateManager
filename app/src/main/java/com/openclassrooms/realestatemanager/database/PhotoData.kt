package com.openclassrooms.realestatemanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_photos")
data class PhotoData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_photo")
    val idPhoto: Int,

    @ColumnInfo(name = "converted_uri")
    var uriConverted: String,

    var name: String,

    @ColumnInfo(name = "id_associated_estate")
    val idAssociatedEstate: Int
)