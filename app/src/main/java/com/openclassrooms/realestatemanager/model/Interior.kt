package com.openclassrooms.realestatemanager.model

/**
 * Defines a real estate interior.
 * @param numberRooms : Number of rooms (including bedrooms and bathrooms)
 * @param numberBathrooms : Number of bathrooms
 * @param numberBedrooms : Number of bedrooms
 * @param surface : surface (sq m)
 */
class Interior(
    var numberRooms: Int,
    var numberBathrooms: Int,
    var numberBedrooms : Int,
    var surface: Int)