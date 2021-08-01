package com.openclassrooms.realestatemanager

import com.openclassrooms.data.entities.*
import com.openclassrooms.realestatemanager.model.*

// Estate converters
fun Estate.toEstateData() = EstateData(type = this.type,
                                       district = this.district, price = this.price,
                                       description = this.description, address = this.address,
                                       status = this.status, idAgent = this.agent.id)

fun EstateData.toEstate(interior: Interior, listPhoto: MutableList<Photo>, agent: Agent,
                        dates: Dates) = Estate(
                            id = this.idEstate, type = this.type,
                            district = this.district, price = this.price,
                            description = this.description, address = this.address,
                            status = this.status, agent = agent,
                            selected = false, interior = interior,
                            listPhoto = listPhoto, dates= dates)


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
    entryDateDay = entryDateDay,
    entryDateMonth = entryDateMonth,
    entryDateYear = entryDateYear
)

fun EntryDateData.toEntryDate() = EntryDate(
    entryDateDay = entryDateDay,
    entryDateMonth = entryDateMonth,
    entryDateYear = entryDateYear
)

fun SaleDate.toSaleDateData() = SaleDateData(
    saleDateDay = saleDateDay,
    saleDateMonth = saleDateMonth,
    saleDateYear = saleDateYear
)

fun SaleDateData.toSaleDate() = SaleDate(
    saleDateDay = saleDateDay,
    saleDateMonth = saleDateMonth,
    saleDateYear = saleDateYear
)