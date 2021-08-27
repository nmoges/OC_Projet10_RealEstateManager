package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_agents" table.
 * @param idAgent : id of an agent
 * @param firstName : first name of an agent
 * @param lastName : last name of an agent
 */
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

