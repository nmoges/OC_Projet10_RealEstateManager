package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param firebaseId : firebase id in a Realtime database
 * @param uriConverted : photo uri
 * @param name : photo name
 */
data class PhotoDataFb(
    val firebaseId: String = "",
    var uriConverted: String = "",
    var name: String = ""
)