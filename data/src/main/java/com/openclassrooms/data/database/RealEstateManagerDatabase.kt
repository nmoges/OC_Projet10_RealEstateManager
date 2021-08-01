package com.openclassrooms.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.entities.*

@Database(entities = [EstateData::class,
                      InteriorData::class,
                      PhotoData::class,
                      AgentData::class,
                      DatesData::class], version = 1, exportSchema = false)
abstract class RealEstateManagerDatabase: RoomDatabase() {

    abstract val estateDao: EstateDao
    abstract val photoDao: PhotoDao
    abstract val interiorDao: InteriorDao
    abstract val agentDao: AgentDao
    abstract val datesDao: DatesDao
    abstract val estateWithPhotosAndInteriorDao: EstateWithPhotosAndInteriorDao
}