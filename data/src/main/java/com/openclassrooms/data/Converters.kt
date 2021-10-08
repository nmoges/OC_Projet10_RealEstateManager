package com.openclassrooms.data

import com.openclassrooms.data.entities.*
import com.openclassrooms.data.firebase.*
import com.openclassrooms.data.model.*

// Estate converters
fun Estate.toEstateData() = EstateData(
    type = this.type,
    price = this.price,
    description = this.description,
    status = this.status,
    idAgent = this.agent.id,
    idFirebase = this.firebaseId)

fun EstateData.toEstate(interior: Interior, listPhoto: MutableList<Photo>, agent: Agent,
                        dates: Dates, location: Location, listPointOfInterest: MutableList<PointOfInterest>) = Estate(
    id = this.idEstate,
    type = this.type,
    price = this.price,
    description = this.description,
    status = this.status,
    agent = agent,
    selected = false,
    interior = interior,
    listPhoto = listPhoto,
    dates= dates,
    location = location,
    listPointOfInterest = listPointOfInterest,
    firebaseId = this.idFirebase)

fun Estate.toEstateDataFb(): EstateDataFb {
    val estateDataFb = EstateDataFb(
        firebaseId = this.firebaseId,
        type = this.type,
        price = this.price,
        description = this.description,
        status = this.status,
        interiorDataFb = this.interior.toInteriorDataFb(),
        locationDataFb = this.location.toLocationDataFb(),
        datesDataFb = this.dates.toDatesDataFb(),
        agentDataFb = this.agent.toAgentDataFb())
    this.listPhoto.forEach {
        estateDataFb.listPhotoDataFb.add(it.toPhotoDataFb())
    }
    this.listPointOfInterest.forEach {
        estateDataFb.listPointOfInterestDataFb.add(it.toPointOfInterestDataFb())
    }
    return estateDataFb
}

fun EstateDataFb.toEstate(): Estate {
    val estate = Estate(
        firebaseId = this.firebaseId,
        type = this.type,
        price = this.price,
        description = this.description,
        status = this.status,
        interior = this.interiorDataFb.toInterior(),
        location = this.locationDataFb.toLocation(),
        dates = this.datesDataFb.toDates(),
        agent = this.agentDataFb.toAgent())
    this.listPhotoDataFb.forEach {
        estate.listPhoto.add(it.toPhoto())
    }
    this.listPointOfInterestDataFb.forEach {
        estate.listPointOfInterest.add(it.toPointOfInterest())
    }
    return estate
}


// Photo converters
fun Photo.toPhotoData(associatedId: Long) = PhotoData(
    uriConverted = this.uriConverted,
    name = this.name,
    associatedId = associatedId)

fun PhotoData.toPhoto() = Photo(
    id = idPhoto,
    uriConverted = this.uriConverted,
    name = this.name)

fun Photo.toPhotoDataFb() = PhotoDataFb(
    uriConverted = this.uriConverted,
    name = this.name
)

fun PhotoDataFb.toPhoto() = Photo(
    uriConverted = this.uriConverted,
    name = this.name
)

// Interior converters
fun Interior.toInteriorData(associatedId: Long) = InteriorData(numberRooms = numberRooms,
    numberBathrooms = numberBathrooms, numberBedrooms = numberBedrooms,
    surface = surface, associatedId = associatedId)

fun InteriorData.toInterior() = Interior(
    id = idInterior, numberRooms = numberRooms,
    numberBedrooms = numberBedrooms, numberBathrooms = numberBathrooms,
    surface = surface)

fun Interior.toInteriorDataFb() = InteriorDataFb(
    numberRooms = this.numberRooms,
    numberBathrooms = this.numberBathrooms,
    numberBedrooms = this.numberBedrooms,
    surface = this.surface
)

fun InteriorDataFb.toInterior() = Interior(
    numberRooms = this.numberRooms,
    numberBedrooms = this.numberBedrooms,
    numberBathrooms = this.numberBathrooms,
    surface = this.surface
)

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

fun Location.toLocationDataFb() = LocationDataFb(
    latitude = this.latitude,
    longitude = this.longitude,
    address = this.address,
    district = this.district
)

fun LocationDataFb.toLocation() = Location(
    latitude = this.latitude,
    longitude = this.longitude,
    address = this.address,
    district = this.district
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

fun PointOfInterest.toPointOfInterestDataFb() = PointOfInterestDataFb(
    name = this.name
)

fun PointOfInterestDataFb.toPointOfInterest() = PointOfInterest(
    name = this.name
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

fun Agent.toAgentDataFb() = AgentDataFb(
    firstName = this.firstName,
    lastName = this.lastName
)

fun AgentDataFb.toAgent() = Agent(
    firstName = this.firstName,
    lastName = this.lastName
)

// Date converters
fun Dates.toDatesData(associatedId: Long) = DatesData(
    dateEntry = dateEntry,
    dateSale = dateSale,
    associatedId = associatedId
)

fun DatesData.toDates() = Dates(
    id = idDates,
    dateSale = dateSale,
    dateEntry = dateEntry
)

fun Dates.toDatesDataFb() = DatesDataFb(
    dateEntry = this.dateEntry,
    dateSale = this.dateSale
)

fun DatesDataFb.toDates() = Dates(
    dateEntry = this.dateEntry,
    dateSale = this.dateSale,
)