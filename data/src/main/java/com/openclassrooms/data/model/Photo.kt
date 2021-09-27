package com.openclassrooms.data.model

import java.util.*

/**
 * Defines a photo.
 * @param uriConverted : Converted uri value associated with a photo.
 * @param name : name associated with a photo.
 */
class Photo(
    var id: Long = 0,
    var uriConverted: String = "",
    var name: String = "",
    val firebaseId: String = UUID.randomUUID().toString())