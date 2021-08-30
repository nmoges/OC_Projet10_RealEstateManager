package com.openclassrooms.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.data.database.RealEstateManagerDatabase

/**
 * Defines an entity for [RealEstateManagerDatabase] "table_estates" table.
 * @param idEstate : id of a row in "table_estates"
 * @param type : type of estate
 * @param price : price of an estate
 * @param description : description of an estate
 * @param idAgent : id of the associated real estate agent
 * @param status : estate status (available or sold)
 */
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
) {
    companion object {
        const val TABLE_NAME = "table_estates"
    }
}