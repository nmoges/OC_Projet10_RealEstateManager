package com.openclassrooms.realestatemanager.service

import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior

/**
 * FOR TESTING ONLY, TO REMOVE WHEN ROOM DATABASE IS IMPLEMENTED
 */
class DummyEstateGenerator {

    companion object {
        val dummyListEstate: MutableList<Estate> = mutableListOf(
            Estate(
                id = 0,
                type = "Penthouse",
                district = "Upper East Side",
                price = 29872000,
                Interior(numberRooms = 8, numberBathrooms = 2, numberBedrooms = 2, surface = 750),
                description = "Real Estate description Penthouse",
                address = "740 Park Avenue\nApt 6/7A\nNew York\nNY 10021\nUnited States",
                nameAgent = "Agent 1",
                status = false,
                selected = false),
            Estate(
                id = 1,
                type = "House",
                district = "Southampton",
                price = 41480000,
                Interior(numberRooms = 10, numberBathrooms = 2, numberBedrooms = 3, surface = 900),
                description = "Real Estate description House",
                address = "748 Park Avenue\nApt 6/7A\nNew York\nNY 10021\nUnited States",
                nameAgent = "Agent 2",
                status = false,
                selected = false),
            Estate(
                id = 2,
                type = "Duplex",
                district = "Brooklyn",
                price = 13990000,
                Interior(numberRooms = 8, numberBathrooms = 1, numberBedrooms = 2, surface = 700),
                description = "Real Estate description Duplex",
                address = "800 Park Avenue\nApt 8\nNew York\nNY 10021\nUnited States",
                nameAgent = "Agent 2",
                status = false,
                selected = false)
        )
    }
}