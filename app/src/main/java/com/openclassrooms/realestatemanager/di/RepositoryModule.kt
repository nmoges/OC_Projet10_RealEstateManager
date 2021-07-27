package com.openclassrooms.realestatemanager.di

import com.openclassrooms.data.dao.EstateDao
import com.openclassrooms.data.dao.EstateWithPhotosAndInteriorDao
import com.openclassrooms.data.dao.InteriorDao
import com.openclassrooms.data.dao.PhotoDao
import com.openclassrooms.data.repository.RealEstateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for [RealEstateRepository] dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(estateDao: EstateDao,
                         photoDao: PhotoDao,
                         interiorDao: InteriorDao,
                         estateWithPhotosAndInteriorDao: EstateWithPhotosAndInteriorDao
    ): RealEstateRepository {
        return RealEstateRepository(estateDao, photoDao, interiorDao, estateWithPhotosAndInteriorDao)
    }
}