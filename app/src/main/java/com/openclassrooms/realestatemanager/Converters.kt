package com.openclassrooms.realestatemanager

import com.openclassrooms.data.entities.EstateData
import com.openclassrooms.data.entities.InteriorData
import com.openclassrooms.data.entities.PhotoData
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
import com.openclassrooms.realestatemanager.model.Photo

class Converters{
    companion object {

        fun convertEstateToEstateData(estate: Estate): EstateData =
            EstateData(
                    idEstate = estate.id,
                    type = estate.type,
                    district = estate.district,
                    price = estate.price,
                    description = estate.description,
                    address = estate.address,
                    nameAgent = estate.nameAgent,
                    status = estate.status
            )

        fun convertEstateDataToEstate(estateData: EstateData,
                                      interior: Interior,
                                      listPhoto: MutableList<Photo>): Estate =
            Estate(id = estateData.idEstate,
                   type = estateData.type,
                   district = estateData.district,
                   price = estateData.price,
                   description = estateData.description,
                   address = estateData.address,
                   status = estateData.status,
                   nameAgent = estateData.nameAgent,
                   selected = false,
                   interior = interior,
                   listPhoto = listPhoto)

        fun convertPhotoToPhotoData(photo: Photo, associatedId: Long): PhotoData =
            PhotoData(
                uriConverted = photo.uriConverted,
                name = photo.name,
                associatedId = associatedId
            )

        fun convertPhotoDataToPhoto(photoData: PhotoData): Photo =
            Photo(
                uriConverted = photoData.uriConverted,
                name = photoData.name
            )

        fun convertInteriorToInteriorData(interior: Interior, associatedId: Long): InteriorData =
            InteriorData(
                idInterior = interior.id,
                numberRooms = interior.numberRooms,
                numberBathrooms = interior.numberBathrooms,
                numberBedrooms = interior.numberBedrooms,
                surface = interior.surface,
                associatedId = associatedId
            )

        fun convertInteriorDataToInterior(interiorData: InteriorData): Interior =
            Interior(
                id = interiorData.idInterior,
                numberRooms = interiorData.numberRooms,
                numberBedrooms = interiorData.numberBedrooms,
                numberBathrooms = interiorData.numberBathrooms,
                surface = interiorData.surface
            )

    }
}