package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_locations" table.
 * @param idLocation : id of a location
 * @param latitude : latitude of a location
 * @param longitude : longitude of a location
 * @param address : address of a location
 * @param district : district of a location
 * @param associatedId : associated estate id
 */
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
) {
    companion object {
        const val TABLE_NAME = "table_locations"
    }
}