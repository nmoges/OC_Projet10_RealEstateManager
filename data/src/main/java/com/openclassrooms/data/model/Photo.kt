package com.openclassrooms.data.model

/**
 * Defines a photo.
 * @param uriConverted : Converted uri value associated with a photo.
 * @param name : name associated with a photo.
 */
class Photo(
    var id: Long = 0,
    var uriConverted: String = "",
    var name: String = "")