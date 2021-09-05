package com.openclassrooms.data

import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.DatesData
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.data.model.*
import com.openclassrooms.data.model.Dates

// Estate converters
fun Estate.toEstateData() = EstateData(idEstate = this.id, type = this.type, price = this.price,
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
fun Interior.toInteriorData(associatedId: Long) = InteriorData(idInterior = this.id, numberRooms = numberRooms,
    numberBathrooms = numberBathrooms, numberBedrooms = numberBedrooms,
    surface = surface, associatedId = associatedId)

fun InteriorData.toInterior() = Interior(
    id = idInterior, numberRooms = numberRooms,
    numberBedrooms = numberBedrooms, numberBathrooms = numberBathrooms,
    surface = surface)

// Location converters
fun Location.toLocationData(associatedId: Long) = LocationData(
    idLocation = id,
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
    idPoi = id,
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
    idDates = id,
    dateEntry = dateEntry,
    dateSale = dateSale,
    associatedId = associatedId
)

fun DatesData.toDates(): Dates = Dates(
    id = idDates,
    dateSale = dateSale,
    dateEntry = dateEntry
)