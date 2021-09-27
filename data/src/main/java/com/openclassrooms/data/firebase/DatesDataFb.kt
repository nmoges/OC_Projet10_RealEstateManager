package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param dateEntry : id of a date
 * @param dateSale : associated estate id
 */
data class DatesDataFb(
    var dateEntry : String = "",
    var dateSale : String = ""
)