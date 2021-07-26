package com.openclassrooms.realestatemanager.di

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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RealEstateManagerDatabase {
        return Room.databaseBuilder(
            context,
            RealEstateManagerDatabase::class.java,
            "real_estate_amanager_database"
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
    fun provideEstateWithPhotosAndInteriorDao(db: RealEstateManagerDatabase) =
                                                                   db.estateWithPhotosAndInteriorDao
}