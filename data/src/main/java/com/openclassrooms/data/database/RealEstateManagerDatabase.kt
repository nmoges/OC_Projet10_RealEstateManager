package com.openclassrooms.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclassrooms.data.dao.EstateDao
import com.openclassrooms.data.dao.EstateWithPhotosAndInteriorDao
import com.openclassrooms.data.dao.InteriorDao
import com.openclassrooms.data.dao.PhotoDao
import com.openclassrooms.data.entities.EstateData
import com.openclassrooms.data.entities.InteriorData
import com.openclassrooms.data.entities.PhotoData

@Database(entities = [EstateData::class, InteriorData::class, PhotoData::class], version = 1, exportSchema = false)
abstract class RealEstateManagerDatabase: RoomDatabase() {

    abstract val estateDao: EstateDao
    abstract val photoDao: PhotoDao
    abstract val interiorDao: InteriorDao
    abstract val estateWithPhotosAndInteriorDao: EstateWithPhotosAndInteriorDao
}