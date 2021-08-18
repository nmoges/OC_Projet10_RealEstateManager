package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.date.DatesData
import com.openclassrooms.data.entities.date.EntryDateData
import com.openclassrooms.data.entities.date.SaleDateData
import com.openclassrooms.realestatemanager.model.*
import com.openclassrooms.realestatemanager.model.date.Dates
import com.openclassrooms.realestatemanager.model.date.EntryDate
import com.openclassrooms.realestatemanager.model.date.SaleDate

// Estate converters
fun Estate.toEstateData() = EstateData(type = this.type, price = this.price,
                                       description = this.description,
                                       status = this.status, idAgent = this.agent.id)

fun EstateData.toEstate(interior: Interior, listPhoto: MutableList<Photo>, agent: Agent,
                        dates: Dates, location: Location, listPointOfInterest: MutableList<PointOfInterest>) = Estate(
                            id = this.idEstate, type = this.type, price = this.price,
                            description = this.description,
                            status = this.status, agent = agent,
                            selected = false, interior = interior,
                            listPhoto = listPhoto, dates= dates, location = location,
                            listPointOfInterest = listPointOfInterest)


// Photo converters
fun Photo.toPhotoData(associatedId: Long) = PhotoData(uriConverted = this.uriConverted,
                                                      name = this.name,
                                                      associatedId = associatedId)
//TODO() : check id
fun PhotoData.toPhoto() = Photo(uriConverted = this.uriConverted, name = this.name)

// Interior converters
fun Interior.toInteriorData(associatedId: Long) = InteriorData(numberRooms = numberRooms,
                                 numberBathrooms = numberBathrooms, numberBedrooms = numberBedrooms,
                                 surface = surface, associatedId = associatedId)

fun InteriorData.toInterior() = Interior(
                                id = idInterior, numberRooms = numberRooms,
                                numberBedrooms = numberBedrooms, numberBathrooms = numberBathrooms,
                                surface = surface)

// Location converters
fun Location.toLocationData(associatedId: Long) = LocationData(
    latitude = latitude,
    longitude = longitude,
    address = address,
    district = district,
    associatedId = associatedId
)

fun LocationData.toLocation() = Location(
    id = idLocation,
    latitude = latitude,
    longitude = longitude,
    address = address,
    district = district
)


// PointOfInterest converters
fun PointOfInterest.toPointOfInterestData(associatedId: Long) = PointOfInterestData(
    name = name,
    associatedId = associatedId
)

fun PointOfInterestData.toPointOfInterest() = PointOfInterest(
    id = idPoi,
    name = name
)

// Agent converters
fun AgentData.toAgent() = Agent(
    id = idAgent,
    firstName = firstName,
    lastName = lastName
)

fun Agent.toAgentData() = AgentData(
    idAgent = id,
    firstName = firstName,
    lastName = lastName
)

// Date converters
fun Dates.toDatesData(associatedId: Long) = DatesData(
    entryDateData = entryDate.toEntryDateData(),
    saleDateData = saleDate.toSaleDateData(),
    associatedId = associatedId
)

fun DatesData.toDates() = Dates(
    id = idDates,
    entryDate = entryDateData.toEntryDate(),
    saleDate = saleDateData.toSaleDate()
)

fun EntryDate.toEntryDateData() = EntryDateData(
    day = day,
    month = month,
    year = year
)

fun EntryDateData.toEntryDate() = EntryDate(
    day = day,
    month = month,
    year = year
)

fun SaleDate.toSaleDateData() = SaleDateData(
    day = day,
    month = month,
    year = year
)

fun SaleDateData.toSaleDate() = SaleDate(
    day = day,
    month = month,
    year = year
)