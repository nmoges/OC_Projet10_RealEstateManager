package com.openclassrooms.data.model

/**
 * Defines dates associated to a real estate (entry date for selling, and sale date)
 * @param id: Id
 * @param dateEntry : date when an estate becomes available for sale
 * @param dateSale : date when an estate has been sold
 */
class Dates(
    var id: Long = 0,
    var dateEntry : String = "",
    var dateSale : String = ""
)