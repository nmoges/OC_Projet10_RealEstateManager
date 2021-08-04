package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "table_estates",
        foreignKeys = [ForeignKey(entity = AgentData::class,
                                  parentColumns = arrayOf("id"),
                                  childColumns = arrayOf("id_agent"))])
data class EstateData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_estate")
    var idEstate: Long = 0,
    var type: String,
    var price: Int,
    var description: String,
    @ColumnInfo(name = "id_agent")
    var idAgent: Long,
    var status: Boolean,
)