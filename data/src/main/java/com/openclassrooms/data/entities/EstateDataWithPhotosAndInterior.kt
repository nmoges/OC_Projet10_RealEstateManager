package com.openclassrooms.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class EstateDataWithPhotosAndInterior(
    @Embedded
    val estateData: EstateData,

    /**
     * Defines an one-to-many relationship between an [EstateData]
     * from table_estates and several [PhotoData] from table_photos.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val listPhotosData: List<PhotoData>,

    /**
     * Defines an one-to-one relationship between an [EstateData] from table_estates
     * and its associated [InteriorData] from table_interiors.
     */
    @Relation(parentColumn = "id_estate", entityColumn = "id_associated_estate")
    val interiorData: InteriorData
)