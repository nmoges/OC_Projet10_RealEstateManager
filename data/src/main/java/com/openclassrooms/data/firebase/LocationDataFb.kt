package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param latitude : latitude of an estate
 * @param longitude : longitude of an estate
 * @param address : address of an estate
 * @param district : district of an estate
 */
data class LocationDataFb(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String = "",
    var district: String = "")