package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param firebaseId : firebase id in a Realtime database
 * @param type : type estate
 * @param price : price of an estate
 * @param interiorDataFb : associated [InteriorDataFb]
 * @param description : description of an estate
 * @param locationDataFb : associated [LocationDataFb]
 * @param agentDataFb : associated [AgentDataFb]
 * @param datesDataFb : associated [DatesDataFb]
 * @param status : status of an estate
 * @param listPhotoDataFb : list of [PhotoDataFb]
 * @param listPointOfInterestDataFb : list of [PointOfInterestDataFb]
 */
data class EstateDataFb(
    var firebaseId: String = "",
    var type: String = "",
    var price: Int = 0,
    var interiorDataFb: InteriorDataFb = InteriorDataFb(numberRooms = 0,
                                                        numberBathrooms = 0,
                                                        numberBedrooms = 0,
                                                        surface = 0),
    var description: String = "",
    var locationDataFb: LocationDataFb = LocationDataFb(latitude = 0.0,
                                                        longitude = 0.0,
                                                        address = "",
                                                        district = ""),
    var agentDataFb: AgentDataFb = AgentDataFb(firstName = "",
                                               lastName = ""),
    var datesDataFb: DatesDataFb = DatesDataFb(dateEntry = "",
                                               dateSale = ""),
    var status: Boolean = false,
    var listPhotoDataFb: MutableList<PhotoDataFb> = mutableListOf(),
    var listPointOfInterestDataFb: MutableList<PointOfInterestDataFb> = mutableListOf()
)