package com.openclassrooms.realestatemanager.model

/**
 * Defines a real estate.
 * @param type : Type of real estate (apartment, loft, etc.)
 * @param district : District of the real estate
 * @param price : Price ($)
 * @param numberRooms : Number of rooms (including bedrooms and bathrooms)
 * @param numberBathrooms : Number of bathrooms
 * @param numberBedrooms : Number of bedrooms
 * @param surface : surface (sq m)
 * @param description : real estate description
 * @param address : location address
 * @param nameAgent : name of the associated real estate agent
 * @param status : Real estate status (sold, or for sale)
 */
class Estate(
    var type: String,
    var district: String,
    var price: String,
    var numberRooms: Int,
    var numberBathrooms: Int,
    var numberBedrooms: Int,
    var surface: Double,
    var description: String,
    var address: String,
    var nameAgent: String,
    var status: Boolean
)