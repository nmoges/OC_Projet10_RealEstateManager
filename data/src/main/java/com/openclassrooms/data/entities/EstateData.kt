package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_estates")
data class EstateData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_estate")
    var idEstate: Long = 0,
    var type: String,
    var district: String,
    var price: Int,
    var description: String,
    var address: String,
    @ColumnInfo(name = "name_agent")
    var nameAgent: String,
    var status: Boolean,
)