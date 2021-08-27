package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_poi" table.
 * @param idPoi : id of a point of interest
 * @param name : name of the point of interest
 * @param associatedId : associated estate id
 */
@Entity(tableName = "table_poi")
data class PointOfInterestData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_poi")
    var idPoi: Long = 0,

    var name: String,

    @ColumnInfo(name= "id_associated_estate")
    var associatedId: Long
)