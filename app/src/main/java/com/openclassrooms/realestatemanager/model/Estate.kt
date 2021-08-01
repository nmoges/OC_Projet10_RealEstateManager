package com.openclassrooms.realestatemanager.model

/**
 * Defines a real estate.
 * @param id : Id of the estate
 * @param type : Type of real estate (apartment, loft, etc.)
 * @param district : District of the real estate
 * @param price : Price ($)
 * @param interior : defines estate interior properties (rooms, surface)
 * @param description : real estate description
 * @param address : location address
 * @param agent :  associated real estate agent
 * @param dates : associated entry and sell dates
 * @param status : Real estate status (sold, or for sale)
 * @param selected : Define if a estate is currently being consulted (true) or not (false)
 */
data class Estate(
    var id: Long,
    var type: String,
    var district: String,
    var price: Int,
    var interior: Interior,
    var description: String,
    var address: String,
    var agent: Agent,
    var dates: Dates,
    var status: Boolean = false,
    var selected: Boolean = false,
    var listPhoto: MutableList<Photo> = mutableListOf()
)