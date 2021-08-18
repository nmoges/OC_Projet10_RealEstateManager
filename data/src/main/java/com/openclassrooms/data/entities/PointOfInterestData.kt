package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_poi")
data class PointOfInterestData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_poi")
    var idPoi: Long = 0,

    var name: String,

    @ColumnInfo(name= "id_associated_estate")
    var associatedId: Long
)