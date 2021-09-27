package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param numberBathrooms : number of bathrooms of an estate
 * @param numberBedrooms : number of bedrooms of an estate
 * @param numberRooms : number of rooms of an estate
 * @param surface : surface of an estate
 */
data class InteriorDataFb(
    var numberRooms: Int = 0,
    var numberBathrooms: Int = 0,
    var numberBedrooms: Int = 0,
    var surface: Int = 0
)