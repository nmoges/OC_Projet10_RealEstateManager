package com.openclassrooms.data.model

/**
 * Defines a real estate interior.
 * @param numberRooms : Number of rooms (including bedrooms and bathrooms)
 * @param numberBathrooms : Number of bathrooms
 * @param numberBedrooms : Number of bedrooms
 * @param surface : surface (sq m)
 */
class Interior(
    var id: Long = 0,
    var numberRooms: Int = 0,
    var numberBathrooms: Int = 0,
    var numberBedrooms : Int = 0,
    var surface: Int = 0)