package com.openclassrooms.data

import com.openclassrooms.data.model.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import junit.framework.Assert.assertEquals

@RunWith(JUnit4::class)
class ModelTest {

    @Test
    fun test_agent_object_creation() {
        val agent = Agent(0, "FirstName", "LastName")
        assertEquals(0, agent.id)
        assertEquals("FirstName", agent.firstName)
        assertEquals("LastName", agent.lastName)
    }

    @Test
    fun test_dates_object_creation() {
        val dates = Dates()
        assertEquals(0, dates.id)
        assertEquals("", dates.dateEntry)
        assertEquals("", dates.dateSale)
    }

    @Test
    fun test_interior_object_creation() {
        val interior = Interior(id = 0,
                                numberRooms = 10,
                                numberBathrooms = 2,
                                numberBedrooms = 3,
                                surface = 50)
        assertEquals(0, interior.id)
        assertEquals(10, interior.numberRooms)
        assertEquals(2, interior.numberBathrooms)
        assertEquals(3, interior.numberBedrooms)
        assertEquals(50, interior.surface)
    }

    @Test
    fun test_location_object_creation() {
        val location = Location(id = 0,
                                latitude = 10.0,
                                longitude = 10.0,
                                address = "123 estate address",
                                district = "estate district")
        assertEquals(0, location.id)
        assertEquals(10.0, location.latitude)
        assertEquals(10.0, location.longitude)
        assertEquals("123 estate address", location.address)
        assertEquals("estate district", location.district)
    }

    @Test
    fun test_photo_object_creation() {
        val photo = Photo(id = 0,
                          uriConverted = "uri_converted_photo",
                          name = "name_photo")
        assertEquals(0, photo.id)
        assertEquals("uri_converted_photo", photo.uriConverted)
        assertEquals("name_photo", photo.name)
    }

    @Test
    fun test_point_of_interest_creation() {
        val pointOfInterest = PointOfInterest(id = 0,
                                              name = "Bar")
        assertEquals(0, pointOfInterest.id)
        assertEquals("Bar", pointOfInterest.name)
    }

    @Test
    fun test_estate_creation() {
        val estate = Estate()
        assertEquals(0, estate.id)
        assertEquals("", estate.type)
        assertEquals(0, estate.price)
        assertEquals("", estate.description)
        assertEquals(false, estate.status)
        assertEquals(false, estate.selected)
        assertEquals(true, estate.firebaseId.isNotEmpty())
        // Interior
        assertEquals(0, estate.interior.id)
        assertEquals(0, estate.interior.numberRooms)
        assertEquals(0, estate.interior.numberBathrooms)
        assertEquals(0, estate.interior.numberBedrooms)
        assertEquals(0, estate.interior.surface)
        // Agent
        assertEquals(0, estate.agent.id)
        assertEquals("", estate.agent.firstName)
        assertEquals("", estate.agent.lastName)
        // Dates
        assertEquals(0, estate.dates.id)
        assertEquals("", estate.dates.dateEntry)
        assertEquals("", estate.dates.dateSale)
        // Photos
        assertEquals(true, estate.listPhoto.isEmpty())
        // Points of interest
        assertEquals(true, estate.listPointOfInterest.isEmpty())
    }
}