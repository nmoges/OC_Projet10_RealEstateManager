package com.openclassrooms.data.model

/**
 * Defines a real estate location.
 * @param id: Id
 * @param latitude : latitude value
 * @param longitude : longitude value
 * @param address : address of the estate
 * @param district : district of the estate
 */
data class Location(
    var id: Long = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String = "",
    var district: String = "")