package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param name : point of interest name
 */
data class PointOfInterestDataFb(
    var name: String = ""
)