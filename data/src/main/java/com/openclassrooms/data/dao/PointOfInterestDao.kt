package com.openclassrooms.data.dao

import androidx.room.*
import com.openclassrooms.data.database.RealEstateManagerDatabase
import com.openclassrooms.data.entities.PointOfInterestData

/**
 * Data access object interface to [RealEstateManagerDatabase] table_poi.
 */
@Dao
interface PointOfInterestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPointOfInterestData(pointOfInterestData: PointOfInterestData)

    @Delete
    suspend fun deletePointOfInterestData(pointOfInterestData: PointOfInterestData)

    @Query("SELECT * FROM table_poi WHERE id_associated_estate = :id")
    suspend fun getPointsOfInterest(id: Long): List<PointOfInterestData>

    @Query("SELECT * FROM table_poi WHERE id_firebase = :id")
    suspend fun getPointOfInterestByFirebaseId(id: String): PointOfInterestData
}