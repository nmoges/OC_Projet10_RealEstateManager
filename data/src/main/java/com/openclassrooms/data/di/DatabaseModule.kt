package com.openclassrooms.data.di

import android.content.Context
import androidx.room.Room
import com.openclassrooms.data.dao.EstateDao
import com.openclassrooms.data.database.RealEstateManagerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for [RealEstateManagerDatabase] dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RealEstateManagerDatabase {
        return Room.databaseBuilder(
            context,
            RealEstateManagerDatabase::class.java,
            "real_estate_manager_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideEstateDao(db: RealEstateManagerDatabase): EstateDao {
        return db.estateDao
    }

    @Singleton
    @Provides
    fun providePhotoDao(db: RealEstateManagerDatabase) = db.photoDao

    @Singleton
    @Provides
    fun provideInteriorDao(db: RealEstateManagerDatabase) = db.interiorDao

    @Singleton
    @Provides
    fun provideFullEstateDao(db: RealEstateManagerDatabase) = db.fullEstateDao

    @Singleton
    @Provides
    fun provideAgentDao(db: RealEstateManagerDatabase) = db.agentDao

    @Singleton
    @Provides
    fun provideDatesDao(db: RealEstateManagerDatabase) = db.datesDao

    @Singleton
    @Provides
    fun provideLocationDao(db: RealEstateManagerDatabase) = db.locationDao

    @Singleton
    @Provides
    fun providePointOfInterestDao(db: RealEstateManagerDatabase) = db.pointOfInterestDao
}