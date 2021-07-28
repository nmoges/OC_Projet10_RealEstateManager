package com.openclassrooms.realestatemanager

import com.openclassrooms.data.entities.EstateData
import com.openclassrooms.data.entities.InteriorData
import com.openclassrooms.data.entities.PhotoData
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
import com.openclassrooms.realestatemanager.model.Photo

// Estate converters
fun Estate.toEstateData() = EstateData(idEstate = this.id, type = this.type,
                                       district = this.district, price = this.price,
                                       description = this.description, address = this.address,
                                       nameAgent = this.nameAgent, status = this.status)

fun EstateData.toEstate(interior: Interior, listPhoto: MutableList<Photo>)
                          = Estate(id = this.idEstate, type = this.type,
                                   district = this.district, price = this.price,
                                   description = this.description, address = this.address,
                                   status = this.status, nameAgent = this.nameAgent,
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