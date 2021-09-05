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
    var id: Long,
    var latitude: Double,
    var longitude: Double,
    var address: String,
    var district: String
)