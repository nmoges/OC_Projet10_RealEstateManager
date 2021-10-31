package com.openclassrooms.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.DatesData

/**
 * Application database class.
 */
@Database(entities = [EstateData::class,
                      InteriorData::class,
                      LocationData ::class,
                      PhotoData::class,
                      AgentData::class,
                      DatesData::class,
                      PointOfInterestData::class], version = 2, exportSchema = false)
abstract class RealEstateManagerDatabase: RoomDatabase() {

    abstract val estateDao: EstateDao
    abstract val photoDao: PhotoDao
    abstract val interiorDao: InteriorDao
    abstract val agentDao: AgentDao
    abstract val datesDao: DatesDao
    abstract val locationDao: LocationDao
    abstract val pointOfInterestDao: PointOfInterestDao
    abstract val fullEstateDao: FullEstateDao
}