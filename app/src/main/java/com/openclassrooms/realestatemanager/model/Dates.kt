package com.openclassrooms.realestatemanager.model

import java.util.*

/**
 * Defines dates associated to a real estate (entry date for selling, and sale date)
 * @param id: Id
 */
class Dates(
    var id: Long,
    var dateEntry : String,
    var dateSale : String
)