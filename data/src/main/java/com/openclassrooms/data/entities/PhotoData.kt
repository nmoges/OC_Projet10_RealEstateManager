package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_photos" table.
 * @param idPhoto : id of a photo
 * @param uriConverted : String-converted uri of a photo
 * @param name : name of the photo
 * @param associatedId : associated estate id
 */
@Entity(tableName = "table_photos")
data class PhotoData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_photo")
    var idPhoto: Long = 0,

    @ColumnInfo(name = "converted_uri")
    var uriConverted: String,

    var name: String,

    @ColumnInfo(name = "id_associated_estate")
    var associatedId: Long
)