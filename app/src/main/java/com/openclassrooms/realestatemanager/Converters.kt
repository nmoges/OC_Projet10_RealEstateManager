package com.openclassrooms.realestatemanager

import com.openclassrooms.data.entities.AgentData
import com.openclassrooms.data.entities.EstateData
import com.openclassrooms.data.entities.InteriorData
import com.openclassrooms.data.entities.PhotoData
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
import com.openclassrooms.realestatemanager.model.Photo

// Estate converters
fun Estate.toEstateData() = EstateData(idEstate = this.id, type = this.type,
                                       district = this.district, price = this.price,
                                       description = this.description, address = this.address,
                                       status = this.status, idAgent = this.agent.id)

fun EstateData.toEstate(interior: Interior, listPhoto: MutableList<Photo>, agent: Agent) = Estate(
                            id = this.idEstate, type = this.type,
                            district = this.district, price = this.price,
                            description = this.description, address = this.address,
                            status = this.status, agent = agent,
                            selected = false, interior = interior,
                            listPhoto = listPhoto)


// Photo converters
fun Photo.toPhotoData(associatedId: Long) = PhotoData(uriConverted = this.uriConverted,
                                                      name = this.name,
                                                      associatedId = associatedId)

fun PhotoData.toPhoto() = Photo(uriConverted = this.uriConverted, name = this.name)

// Interior converters
fun Interior.toInteriorData(associatedId: Long) = InteriorData(
                                 idInterior = id, numberRooms = numberRooms,
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