package com.openclassrooms.data

import com.openclassrooms.data.model.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import junit.framework.Assert.assertEquals

@RunWith(JUnit4::class)
class ConvertersTest {

    @Test
    fun test_estate_converters() {
        val estate = Estate()
        val estateData = estate.toEstateData()
        val estateDataFb = estate.toEstateDataFb()

        // Check Estate -> EstateData conversion
        assertEquals(estate.id, estateData.idEstate)
        assertEquals(estate.description, estateData.description)
        assertEquals(estate.price, estateData.price)
        assertEquals(estate.type, estateData.type)
        assertEquals(estate.status, estateData.status)
        assertEquals(estate.firebaseId, estateData.idFirebase)

        // Check Estate -> EstateDataFb conversion
        assertEquals(estate.firebaseId, estateDataFb.firebaseId)
        assertEquals(estate.type, estateDataFb.type)
        assertEquals(estate.price, estateDataFb.price)
        assertEquals(estate.status, estateDataFb.status)
        assertEquals(estate.description, estateDataFb.description)
        assertEquals(estate.interior.surface, estateDataFb.interiorDataFb.surface)
        assertEquals(estate.interior.numberRooms, estateDataFb.interiorDataFb.numberRooms)
        assertEquals(estate.interior.numberBathrooms, estateDataFb.interiorDataFb.numberBathrooms)
        assertEquals(estate.interior.numberBedrooms, estateDataFb.interiorDataFb.numberBedrooms)
        assertEquals(estate.location.district, estateDataFb.locationDataFb.district)
        assertEquals(estate.location.longitude, estateDataFb.locationDataFb.longitude)
        assertEquals(estate.location.latitude, estateDataFb.locationDataFb.latitude)
        assertEquals(estate.location.address, estateDataFb.locationDataFb.address)
        assertEquals(estate.agent.firstName, estateDataFb.agentDataFb.firstName)
        assertEquals(estate.agent.lastName, estateDataFb.agentDataFb.lastName)
        assertEquals(estate.listPhoto.size, estateDataFb.listPhotoDataFb.size)
        assertEquals(estate.listPointOfInterest.size, estateDataFb.listPointOfInterestDataFb.size)

        // Check EstateData -> Estate conversion
        var convertedEstate = estateData.toEstate(interior = Interior(),
                                                  location = Location(),
                                                  agent = Agent(),
                                                  dates = Dates(),
                                                  listPhoto = mutableListOf(),
                                                  listPointOfInterest = mutableListOf())
        assertEquals(estateData.idFirebase, convertedEstate.firebaseId)
        assertEquals(estateData.description, convertedEstate.description)
        assertEquals(estateData.type, convertedEstate.type)
        assertEquals(estateData.price, convertedEstate.price)
        assertEquals(estateData.status, convertedEstate.status)

        // Check EstateDataFb -> Estate conversion
        convertedEstate = estateDataFb.toEstate()
        assertEquals(estateDataFb.firebaseId, convertedEstate.firebaseId)
        assertEquals(estateDataFb.description, convertedEstate.description)
        assertEquals(estateDataFb.type, convertedEstate.type)
        assertEquals(estateDataFb.price, convertedEstate.price)
        assertEquals(estateDataFb.status, convertedEstate.status)
    }

    @Test
    fun test_photo_converters() {
        val photo = Photo(id = 1, uriConverted = "uri_value", name = "name_value")
        val photoData = photo.toPhotoData(1)
        val photoDataFb = photo.toPhotoDataFb()

        // Check Photo -> PhotoData conversion
        assertEquals(photo.name, photoData.name)
        assertEquals(photo.uriConverted, photoData.uriConverted)

        // Check Photo -> PhotoDataFb conversion
        assertEquals(photo.name, photoDataFb.name)
        assertEquals(photo.uriConverted, photoDataFb.uriConverted)

        // Check PhotoData -> Photo conversion
        var convertedPhoto = photoData.toPhoto()
        assertEquals(photoData.idPhoto, convertedPhoto.id)
        assertEquals(photoData.name, convertedPhoto.name)
        assertEquals(photoData.uriConverted, convertedPhoto.uriConverted)

        // Check PhotoDataFb -> Photo conversion
        convertedPhoto = photoDataFb.toPhoto()
        assertEquals(photoDataFb.name, convertedPhoto.name)
        assertEquals(photoDataFb.uriConverted, convertedPhoto.uriConverted)
    }

    @Test
    fun test_poi_converters() {
        val poi = PointOfInterest(id = 1, name = "Bar")
        val poiData = poi.toPointOfInterestData(1)
        val poiDataFb = poi.toPointOfInterestDataFb()

        // Check PointOfInterest -> PointOfInterestData conversion
        assertEquals(poi.name, poiData.name)

        // Check PointOfInterest -> PointOfInterestDataFb conversion
        assertEquals(poi.name, poiDataFb.name)

        // Check PointOfInterestData -> PointOfInterest conversion
        var convertedPoi = poiData.toPointOfInterest()
        assertEquals(poiData.name, convertedPoi.name)
        assertEquals(poiData.idPoi, convertedPoi.id)

        // Check PointOfInterestDataFb -> PointOfInterest conversion
        convertedPoi = poiDataFb.toPointOfInterest()
        assertEquals(poiData.name, convertedPoi.name)
        assertEquals(poiData.idPoi, convertedPoi.id)
    }

    @Test
    fun test_agent_converters() {
        val agent = Agent(id = 1, firstName = "first_name_value", lastName = "last_name_value")
        val agentData = agent.toAgentData()
        val agentDataFb = agent.toAgentDataFb()

        // Check Agent -> AgentData conversion
        assertEquals(agent.id, agentData.idAgent)
        assertEquals(agent.firstName, agentData.firstName)
        assertEquals(agent.lastName, agentData.lastName)

        // Check Agent -> AgentDataFb conversion
        assertEquals(agent.firstName, agentDataFb.firstName)
        assertEquals(agent.lastName, agentDataFb.lastName)

        // Check AgentData -> Agent conversion
        var agentConverted = agentData.toAgent()
        assertEquals(agentData.firstName, agentConverted.firstName)
        assertEquals(agentData.lastName, agentConverted.lastName)

        // Check AgentDataFb -> Agent conversion
        agentConverted = agentDataFb.toAgent()
        assertEquals(agentData.firstName, agentConverted.firstName)
        assertEquals(agentData.lastName, agentConverted.lastName)
    }

    @Test
    fun test_interior_converters() {
        val interior = Interior()
        val interiorData = interior.toInteriorData(1)
        val interiorDataFb = interior.toInteriorDataFb()

        // Check Interior -> InteriorData conversion
        assertEquals(interior.id, interiorData.idInterior)
        assertEquals(interior.surface, interiorData.surface)
        assertEquals(interior.numberBedrooms, interiorData.numberBedrooms)
        assertEquals(interior.numberBathrooms, interiorData.numberBathrooms)
        assertEquals(interior.numberRooms, interiorData.numberRooms)

        // Check Interior -> InteriorDataFb conversion
        assertEquals(interior.surface, interiorDataFb.surface)
        assertEquals(interior.numberBedrooms, interiorDataFb.numberBedrooms)
        assertEquals(interior.numberBathrooms, interiorDataFb.numberBathrooms)
        assertEquals(interior.numberRooms, interiorDataFb.numberRooms)

        // Check InteriorData -> Interior conversion
        var convertedInterior = interiorData.toInterior()
        assertEquals(interiorData.surface, convertedInterior.surface)
        assertEquals(interiorData.numberBedrooms, convertedInterior.numberBedrooms)
        assertEquals(interiorData.numberBathrooms, convertedInterior.numberBathrooms)
        assertEquals(interiorData.numberRooms, convertedInterior.numberRooms)

        // Check InteriorDataFb -> Interior conversion
        convertedInterior = interiorDataFb.toInterior()
        assertEquals(interiorDataFb.surface, convertedInterior.surface)
        assertEquals(interiorDataFb.numberBedrooms, convertedInterior.numberBedrooms)
        assertEquals(interiorDataFb.numberBathrooms, convertedInterior.numberBathrooms)
        assertEquals(interiorDataFb.numberRooms, convertedInterior.numberRooms)
    }

    @Test
    fun test_location_converters() {
        val location = Location()
        val locationData = location.toLocationData(1)
        val locationDataFb = location.toLocationDataFb()

        // Check Location -> LocationData conversion
        assertEquals(location.id, locationData.idLocation)
        assertEquals(location.district, locationData.district)
        assertEquals(location.address, locationData.address)
        assertEquals(location.latitude, locationData.latitude)
        assertEquals(location.longitude, locationData.longitude)

        // Check Location -> LocationDataFb conversion
        assertEquals(location.district, locationDataFb.district)
        assertEquals(location.address, locationDataFb.address)
        assertEquals(location.latitude, locationDataFb.latitude)
        assertEquals(location.longitude, locationDataFb.longitude)

        // Check LocationData -> Location conversion
        var convertedLocation = locationData.toLocation()
        assertEquals(locationData.district, convertedLocation.district)
        assertEquals(locationData.address, convertedLocation.address)
        assertEquals(locationData.latitude, convertedLocation.latitude)
        assertEquals(locationData.longitude, convertedLocation.longitude)

        // Check LocationDataFb -> Location conversion
        convertedLocation = locationDataFb.toLocation()
        assertEquals(locationDataFb.district, convertedLocation.district)
        assertEquals(locationDataFb.address, convertedLocation.address)
        assertEquals(locationDataFb.latitude, convertedLocation.latitude)
        assertEquals(locationDataFb.longitude, convertedLocation.longitude)
    }

    @Test
    fun test_dates_converters() {
        val dates = Dates()
        val datesData = dates.toDatesData(1)
        val datesDataFb = dates.toDatesDataFb()

        // Check Dates -> DatesData conversion
        assertEquals(dates.id, datesData.idDates)
        assertEquals(dates.dateEntry, datesData.dateEntry)
        assertEquals(dates.dateSale, datesData.dateSale)

        // Check Dates -> DatesDataFb conversion
        assertEquals(dates.dateEntry, datesDataFb.dateEntry)
        assertEquals(dates.dateSale, datesDataFb.dateSale)

        // Check DatesData -> Dates conversion
        var convertedDates = datesData.toDates()
        assertEquals(datesData.dateEntry, convertedDates.dateEntry)
        assertEquals(datesData.dateSale, convertedDates.dateSale)

        // Check DatesDataFb -> Dates conversion
        convertedDates = datesDataFb.toDates()
        assertEquals(datesDataFb.dateEntry, convertedDates.dateEntry)
        assertEquals(datesDataFb.dateSale, convertedDates.dateSale)
    }
}