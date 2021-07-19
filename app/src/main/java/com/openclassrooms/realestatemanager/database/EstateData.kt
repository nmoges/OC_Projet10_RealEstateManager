package com.openclassrooms.realestatemanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_estates")
data class EstateData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_estate")
    val idEstate: Int,

    var type: String,

    var district: String,

    var price: Int,

    var description: String,

    var address: String,

    @ColumnInfo(name = "name_agent")
    var nameAgent: String,

    var status: Boolean,
)