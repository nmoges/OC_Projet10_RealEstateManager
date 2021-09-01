package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.BuildConfig

object MapHandler {

    /**
     * Provides an Uri.
     * @param latitude : estate latitude
     * @param longitude : estate longitude
     * @param size : size
     * @param zoom : zoom level
     */
    fun getLocationEstateUri(latitude: Double, longitude: Double, size: Int, zoom: Int): String {
        val baseUri = "https://maps.googleapis.com/maps/api/staticmap?"
        val latitudeUri = latitude.toString()
        val longitudeUri = longitude.toString()
        return baseUri + "center=$latitudeUri,$longitudeUri" +
                "&size=${size}x${size}&zoom=${zoom}&markers=color:red%7C$latitudeUri,$longitudeUri" +
                "&key=${BuildConfig.MAPS_API_KEY}"
    }
}
