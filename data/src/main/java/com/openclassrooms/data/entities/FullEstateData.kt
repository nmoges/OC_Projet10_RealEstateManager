package com.openclassrooms.data.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Defines all relations between an [EstateData] from table_estates,
 * and its corresponding rows in others tables.
 */
data class FullEstateData(
    @Embedded
    val estateData: EstateData?,

    /**
     * Defines an one-to-many relationship between an [EstateData]
     * from table_estates and several [PhotoData] from table_photos.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val listPhotosData: List<PhotoData>?,

    /**
     * Defines an one-to-one relationship between an [EstateData] from table_estates
     * and its associated [InteriorData] from table_interiors.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val interiorData: InteriorData?,

    /**
     * Defines an one-to-one relationship between an [EstateData] from table_estates
     * and its associated [DatesData] from table_dates.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val datesData: DatesData?,

    /**
     * Defines an one-to-one relationship between an [EstateData] from table_estates
     * and its associated [LocationData] from table_locations.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val locationData: LocationData?,

    /**
     * Defines an one-to-many relationship between an [EstateData]
     * from table_estates and several [PointOfInterestData] from table_photos.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val listPointOfInterestData : List<PointOfInterestData>?,
)