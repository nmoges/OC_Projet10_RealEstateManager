package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_photos")
data class PhotoData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_photo")
    var idPhoto: Int = 0,

    @ColumnInfo(name = "converted_uri")
    var uriConverted: String,

    var name: String,

    @ColumnInfo(name = "id_associated_estate")
    var associatedId: Long
)