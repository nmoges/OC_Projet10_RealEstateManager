package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EstateData::class, InteriorData::class, PhotoData::class], version = 1)
abstract class RealEstateManagerDatabase: RoomDatabase() {

    companion object {
        // Singleton
        @Volatile private var instanceDatabase: RealEstateManagerDatabase? = null

        /**
         * Gets database singleton.
         */
        fun getDatabase(context: Context): RealEstateManagerDatabase {
            return instanceDatabase ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                                                    RealEstateManagerDatabase::class.java,
                                              "real_estate_amanager_database")
                                    .fallbackToDestructiveMigration()
                                    .build()
                instanceDatabase = instance
                // return instance
                instance
            }
        }
    }
}