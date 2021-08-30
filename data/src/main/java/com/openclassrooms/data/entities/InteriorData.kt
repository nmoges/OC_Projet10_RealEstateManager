package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_interiors" table.
 * @param idInterior : id of an estate interior
 * @param numberRooms : number of rooms
 * @param numberBathrooms : number of bathrooms
 * @param numberBedrooms : number of bedrooms
 * @param surface : estate surface in sqm
 * @param associatedId : associated estate id
 */

@Entity(tableName = "table_interiors")
data class InteriorData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_interior")
    var idInterior: Long = 0,

    @ColumnInfo(name = "number_rooms")
    var numberRooms: Int,

    @ColumnInfo(name = "number_bathrooms")
    var numberBathrooms: Int,

    @ColumnInfo(name = "number_bedrooms")
    var numberBedrooms: Int,

    var surface: Int,

    @ColumnInfo(name = "id_associated_estate")
    var associatedId: Long
) {
    companion object {
        const val TABLE_NAME = "table_interiors"
    }
}