package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param firebaseId : firebase id in a Realtime database
 * @param name : point of interest name
 */
data class PointOfInterestDataFb(
    val firebaseId: String = "",
    var name: String = ""
)