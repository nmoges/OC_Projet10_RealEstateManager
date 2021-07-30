package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_agents")
data class AgentData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var idAgent: Long,
    @ColumnInfo(name = "first_name")
    var firstName: String,
    @ColumnInfo(name = "last_name")
    var lastName: String
)

