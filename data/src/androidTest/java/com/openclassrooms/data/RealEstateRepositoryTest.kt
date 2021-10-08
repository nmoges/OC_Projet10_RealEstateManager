package com.openclassrooms.data

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.data.dao.*
import com.openclassrooms.data.database.RealEstateManagerDatabase
import com.openclassrooms.data.di.DatabaseModule
import com.openclassrooms.data.di.RepositoryModule
import com.openclassrooms.data.model.*
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//TODO() : Utiliser injection Hilt pour repository
@RunWith(AndroidJUnit4::class)
class RealEstateRepositoryTest {

    lateinit var context: Context
    lateinit var db: RealEstateManagerDatabase
    lateinit var repositoryAccess: RealEstateRepositoryAccess
    lateinit var estateDao: EstateDao
    lateinit var interiorDao: InteriorDao
    lateinit var datesDao: DatesDao
    lateinit var locationDao: LocationDao
    lateinit var agentDao: AgentDao
    lateinit var pointOfInterestDao: PointOfInterestDao
    lateinit var photoDao: PhotoDao
    lateinit var fullEstateDao: FullEstateDao
    lateinit var estate: Estate

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, RealEstateManagerDatabase::class.java).build()
        DatabaseModule.let {
            estateDao = it.provideEstateDao(db)
            interiorDao = it.provideInteriorDao(db)
            agentDao = it.provideAgentDao(db)
            datesDao = it.provideDatesDao(db)
            locationDao = it.provideLocationDao(db)
            pointOfInterestDao = it.providePointOfInterestDao(db)
            photoDao = it.providePhotoDao(db)
            fullEstateDao = it.provideFullEstateDao(db)
         //   FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
            repositoryAccess = RepositoryModule.provideRepository(
                                                    estateDao = estateDao,
                                                    photoDao = photoDao,
                                                    interiorDao = interiorDao,
                                                    locationDao = locationDao,
                                                    agentDao = agentDao,
                                                    datesDao = datesDao,
                                                    pointOfInterestDao = pointOfInterestDao,
                                                    fullEstateDao = fullEstateDao,
                                                    context = context)
        }
        initializeData()
    }

    private fun initializeData() {
        val interior = Interior(id = 0, numberRooms = 6, numberBedrooms = 3,
                                numberBathrooms = 1, surface = 100)
        val agent = Agent(id = 0, firstName = "Clark", lastName = "Kent")
        val location = Location(id = 0, address = "78 Av. des Champs-Élysées, 75008 Paris, France",
                            district = "Paris", latitude = 48.87144620000001, longitude = 2.3045534)
        val listPhotos = mutableListOf(
            Photo(id = 0, name = "Kitchen",
                uriConverted = "https://firebasestorage.googleapis.com/v0/b/real-estate-manager-1ee3" +
                        "6.appspot.com/o/images%2Fusers%2FAM5XBuhEikeVtWO3ochH12sHQt72%2FKitchen.jpg" +
                        "?alt=media&token=ec07ca24-3443-44cb-bc91-46e97045231a"),
            Photo(id = 1, name = "Living room",
            uriConverted = "https://firebasestorage.googleapis.com/v0/b/real-estate-manager-1ee36.ap" +
                    "pspot.com/o/images%2Fusers%2FAM5XBuhEikeVtWO3ochH12sHQt72%2FLiving%20room.jpg?a" +
                    "lt=media&token=facfce35-1ddc-4d98-bd14-abf186bd6a51")
        )
        val dates = Dates(id = 0, dateEntry = "2021-10-05", dateSale = "")
        val listPointOfInterest = mutableListOf(
            PointOfInterest(id = 0, name = "School"),
            PointOfInterest(id = 1, name = "Restaurant"),
            PointOfInterest(id = 2, name = "Park")
        )
        estate = Estate(id = 0, type = "House", price = 2500000, description = "Description house",
                        interior = interior, location = location, agent = agent, dates = dates,
                        listPhoto = listPhotos, listPointOfInterest = listPointOfInterest)
    }

    @Test
    fun test_insert_estate_in_room_database() {
     //   runBlockingTest {
           /* repositoryAccess.let {
                it.insertEstate(estate)
                it.insertInterior(estate.interior, estate.id)
                it.insertLocation(estate.location, estate.id)
                it.insertAgent(estate.agent)
                it.insertDates(estate.dates, estate.id)
                estate.listPhoto.forEach { photo ->
                    it.insertPhoto(photo, estate.id)
                }
                estate.listPointOfInterest.forEach { poi ->
                    it.insertPointOfInterest(poi, estate.id)
                }
            }*/
       // }
    }

    @After
    fun closeDatabase() {
        // db.close()
    }
}