package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.data.entities.*
import com.openclassrooms.data.entities.date.DatesData
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.model.*
import com.openclassrooms.realestatemanager.model.date.Dates

object Converters {
    /**
     * Handles conversion of a list of [FullEstateData] object to a list of [Estate].
     * @param list : list to convert
     * @param repositoryAccess : repository for database access
     */
    suspend fun convertListFullEstateDataToListEstate(list: List<FullEstateData>,
                                                      repositoryAccess: RealEstateRepositoryAccess): MutableList<Estate>{
        val listConverted: MutableList<Estate> = mutableListOf()
        list.forEach { it ->
            val interior = it.interiorData.toInterior()
            val listPhoto: MutableList<Photo> = mutableListOf()
            it.listPhotosData.forEach {
                listPhoto.add(it.toPhoto())
            }
            val listPointOfInterest: MutableList<PointOfInterest> = mutableListOf()
            it.listPointOfInterestData.forEach {
                listPointOfInterest.add(it.toPointOfInterest())
            }
            val agent = repositoryAccess.getAgentById(it.estateData.idAgent).toAgent()
            val dates = repositoryAccess.getDatesById(it.estateData.idEstate).toDates()
            val location = it.locationData.toLocation()
            val estate = it.estateData.toEstate(interior, listPhoto, agent, dates,
                location, listPointOfInterest)
            listConverted.add(estate)
        }
        return listConverted
    }
}


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
    dateEntry = dateEntry,
    dateSale = dateSale,
    associatedId = associatedId
)

fun DatesData.toDates() = Dates(
    id = idDates,
    dateSale = dateSale,
    dateEntry = dateEntry
)