package com.openclassrooms.data.model

import java.util.*

/**
 * Defines a real estate.
 * @param id : Id of the estate
 * @param type : Type of real estate (apartment, loft, etc.)
 * @param price : Price ($)
 * @param interior : defines estate interior properties (rooms, surface)
 * @param description : real estate description
 * @param agent :  associated real estate agent
 * @param dates : associated entry and sell dates
 * @param status : Real estate status (sold, or for sale)
 * @param selected : Define if a estate is currently being consulted (true) or not (false)
 * @param listPhoto : List of photos
 * @param listPointOfInterest : List of points of interest
 */
data class Estate(
    var id: Long = 0,
    var type: String = "",
    var price: Int = 0,
    var interior: Interior = Interior(0,0, 0, 0, 0),
    var description: String = "",
    var location: Location = Location(0, 0.0, 0.0, "", ""),
    var agent: Agent = Agent(0, "", ""),
    var dates: Dates = Dates(0, "", ""),
    var status: Boolean = false,
    var selected: Boolean = false,
    var listPhoto: MutableList<Photo> = mutableListOf(),
    var listPointOfInterest: MutableList<PointOfInterest> = mutableListOf(),
    var firebaseId: String = UUID.randomUUID().toString())
