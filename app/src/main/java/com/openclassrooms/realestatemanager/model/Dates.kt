package com.openclassrooms.realestatemanager.model

/**
 * Defines dates associated to a real estate (entry date for selling, and sale date)
 * @param id: Id
 * @param entryDate : real estate entry date
 * @param saleDate : real estate sale date
 */
class Dates(
    var id: Long,
    var entryDate: EntryDate,
    var saleDate: SaleDate
)