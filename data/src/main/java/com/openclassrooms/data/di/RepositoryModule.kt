package com.openclassrooms.data.di

import com.openclassrooms.data.dao.*
import com.openclassrooms.data.repository.RealEstateRepository
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
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
                          locationDao: LocationDao,
                          agentDao: AgentDao,
                          datesDao: DatesDao,
                          pointOfInterestDao: PointOfInterestDao,
                          fullEstateDao: FullEstateDao
    ): RealEstateRepositoryAccess {
        return RealEstateRepository(estateDao, photoDao, interiorDao, locationDao,
                                    agentDao, datesDao, pointOfInterestDao,
                                    fullEstateDao)
    }
}