package com.openclassrooms.data

import com.openclassrooms.data.database.RealEstateManagerDatabase
import com.openclassrooms.data.model.*
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.data.repository.RealEstateRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Test file for [RealEstateRepository] file.
 */
@HiltAndroidTest
class RoomDatabaseAccessTest {

    @get: Rule val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var repositoryAccess: RealEstateRepositoryAccess
    @Inject lateinit var db: RealEstateManagerDatabase

    lateinit var estate: Estate
    lateinit var interior: Interior
    lateinit var agent: Agent
    lateinit var location: Location
    lateinit var listPhotos: MutableList<Photo>
    lateinit var listPointOfInterest: MutableList<PointOfInterest>
    lateinit var dates: Dates

    @Before
    fun setUp() {
        hiltRule.inject()
        initializeData()
    }

    /**
     * Tests data insertion in Room database table_agents.
     */
    @Test
    fun test_insert_agent_in_room_database() {
        runBlocking {
            val id = repositoryAccess.insertAgent(agent)
            val agentFromDb = repositoryAccess.getAgentById(id)
            assertNotNull(agentFromDb)
            assertEquals(agent.firstName, agentFromDb.firstName)
            assertEquals(agent.lastName, agentFromDb.lastName)
        }
    }

    /**
     * Tests data insertion in Room database table_interiors.
     */
    @Test
    fun test_insert_and_update_interior_in_room_database() {
        runBlocking {
            // INSERTION
            val id = repositoryAccess.insertInterior(interior, 1)
            var interiorFromDb = repositoryAccess.getInteriorById(id)
            assertNotNull(interiorFromDb)
            assertEquals(interior.surface, interiorFromDb.surface)
            assertEquals(interior.numberRooms, interiorFromDb.numberRooms)
            assertEquals(interior.numberBathrooms, interiorFromDb.numberBathrooms)
            assertEquals(interior.numberBedrooms, interiorFromDb.numberBedrooms)
            // UPDATE
            interiorFromDb.numberRooms = 10
            repositoryAccess.updateInterior(interiorFromDb, 1)
            interiorFromDb = repositoryAccess.getInteriorById(id)
            assertEquals(10, interiorFromDb.numberRooms)
        }
    }

    /**
     * Tests data insertion in Room database table_locations.
     */
    @Test
    fun test_insert_and_update_location_in_room_database() {
        runBlocking {
            // INSERTION
            val id = repositoryAccess.insertLocation(location, 1)
            var locationFromDb = repositoryAccess.getLocationById(id)
            assertNotNull(locationFromDb)
            assertEquals(location.address, locationFromDb.address)
            assertEquals(location.district, locationFromDb.district)
            assertEquals(location.longitude, locationFromDb.longitude, 0.0)
            assertEquals(location.latitude, locationFromDb.latitude, 0.0)
            // UPDATE
            locationFromDb.district = "Nice"
            repositoryAccess.updateLocation(locationFromDb, 1)
            locationFromDb = repositoryAccess.getLocationById(id)
            assertEquals("Nice", locationFromDb.district)
        }
    }

    /**
     * Tests data insertion in Room database table_dates.
     */
    @Test
    fun test_insert_and_update_dates_in_room_database() {
        runBlocking {
            val id = repositoryAccess.insertDates(dates, 1)
            val datesFromDb = repositoryAccess.getDatesById(id)
            assertNotNull(datesFromDb)
            datesFromDb?.let {
                assertEquals(dates.dateEntry, it.dateEntry)
                assertEquals(dates.dateSale, it.dateSale)
            }
            datesFromDb?.dateSale = "2021-11-01"
            datesFromDb?.let {
                repositoryAccess.updateDates(it, 1)
                val updatedDate = repositoryAccess.getDatesById(id)
                updatedDate?.let { date ->
                    assertEquals("2021-11-01", date.dateSale)
                }
            }
        }
    }

    /**
     * Tests data insertion in Room database table_photos.
     */
    @Test
    fun test_insert_photos_in_room_database() {
        runBlocking {
            listPhotos.forEach { repositoryAccess.insertPhoto(it, 1) }
            val listFromDb = repositoryAccess.getPhotos(1)
            assertNotNull(listFromDb)
            assertEquals(listPhotos.size, listFromDb.size)
            for (i in listFromDb.indices) {
                assertEquals(listPhotos[i].name, listFromDb[i].name)
                assertEquals(listPhotos[i].uriConverted, listFromDb[i].uriConverted)
            }
        }
    }

    /**
     * Tests data insertion in Room database table_poi.
     */
    @Test
    fun test_insert_and_delete_point_of_interest_in_room_database() {
        runBlocking {
            // INSERT
            listPointOfInterest.forEach { repositoryAccess.insertPointOfInterest(it, 1) }
            var listFromDb = repositoryAccess.getPointsOfInterest(1)
            assertNotNull(listFromDb)
            for (i in listFromDb.indices) {
                assertEquals(listPointOfInterest[i].name, listFromDb[i].name)
            }
            // DELETE
            assertEquals(3, listFromDb.size)
            repositoryAccess.deletePointOfInterest(listPointOfInterest[1], 1)
            listFromDb = repositoryAccess.getPointsOfInterest(1)
            assertEquals(2, listFromDb.size)
        }
    }

    /**
     * Tests data insertion in Room database table_estates.
     */
    @Test
    fun test_insert_estate_in_room_database() {
        runBlocking {
            val agent = Agent(id = 1, firstName = "Clark", lastName = "Kent")
            repositoryAccess.let {
                // INSERTION
                it.insertAgent(agent)
                it.insertEstate(estate)
                val firebaseId = estate.firebaseId
                val estateFromDb = it.getEstateWithFirebaseId(firebaseId)
                assertNotNull(estateFromDb)
            }
         }
    }

    @After
    fun closeDatabase() {
        db.clearAllTables()
        db.close()
    }

    /**
     * Initializes a set of data to insert in database.
     */
    private fun initializeData() {
        interior = Interior(id = 1, numberRooms = 6, numberBedrooms = 3,
                            numberBathrooms = 1, surface = 100)
        agent = Agent(id = 1, firstName = "Clark", lastName = "Kent")
        location = Location(id = 1, address = "78 Av. des Champs-Élysées, 75008 Paris, France",
                            district = "Paris", latitude = 48.87144620000001, longitude = 2.3045534)
        listPhotos = mutableListOf(
            Photo(id = 1, name = "Kitchen",
                  uriConverted = "https://firebasestorage.googleapis.com/v0/b/real-estate-manager-1ee3" +
                        "6.appspot.com/o/images%2Fusers%2FAM5XBuhEikeVtWO3ochH12sHQt72%2FKitchen.jpg" +
                        "?alt=media&token=ec07ca24-3443-44cb-bc91-46e97045231a"),
            Photo(id = 2, name = "Living room",
                  uriConverted = "https://firebasestorage.googleapis.com/v0/b/real-estate-manager-1ee36.ap" +
                        "pspot.com/o/images%2Fusers%2FAM5XBuhEikeVtWO3ochH12sHQt72%2FLiving%20room.jpg?a" +
                        "lt=media&token=facfce35-1ddc-4d98-bd14-abf186bd6a51")
        )
        dates = Dates(id = 1, dateEntry = "2021-10-05", dateSale = "")
        listPointOfInterest = mutableListOf(
            PointOfInterest(id = 1, name = "School"),
            PointOfInterest(id = 2, name = "Restaurant"),
            PointOfInterest(id = 3, name = "Park")
        )
        estate = Estate(id = 1, type = "House", price = 2500000, description = "Description house",
                        interior = interior, location = location, agent = agent, dates = dates,
                        listPhoto = listPhotos, listPointOfInterest = listPointOfInterest)
    }
}