package com.openclassrooms.realestatemanager.utils.poi

/**
 * Object defining a set of functions to handle provide point of interest values.
 */
object POIProvider {

    /**
     * Provides the associated point of interest string
     * @param which : Int
     */
    fun providePointOfInterest(which: Int): String {
        var pointOfInterest = ""
        when(which) {
            0 -> pointOfInterest = "Bar"
            1 -> pointOfInterest = "Cafe"
            2 -> pointOfInterest = "Library"
            3 -> pointOfInterest = "Market"
            4 -> pointOfInterest = "Museum"
            5 -> pointOfInterest = "Park"
            6 -> pointOfInterest = "Restaurant"
            7 -> pointOfInterest = "School"
        }
        return pointOfInterest
    }

    /**
     * Provides the associated point of interest index
     * @param pointOfInterest : String
     */
    fun provideIndexFromPointOfInterest(pointOfInterest: String): Int {
        var index = 0
        when(pointOfInterest) {
            "Bar" -> index = 0
            "Cafe"-> index = 1
            "Library"-> index = 2
            "Market"-> index = 3
            "Museum"-> index = 4
            "Park"-> index = 5
            "Restaurant"-> index = 6
            "School"-> index = 7
        }
        return index
    }
}