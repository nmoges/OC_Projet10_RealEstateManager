package com.openclassrooms.realestatemanager.utils.poi

import com.openclassrooms.data.model.PointOfInterest

/**
 * Object defining a set of functions to compare points of interest.
 */
object POIComparator {

    /**
     * Checks if an item of a mutable list of [PointOfInterest] has the same fields
     * that a given [PointOfInterest].
     * @param poi : [PointOfInterest] to compare with the list
     * @param list : list of [PointOfInterest]
     */
    fun containsPOI(poi: PointOfInterest, list: MutableList<PointOfInterest>): Boolean {
        var check = false
        var index = 0
        while (index < list.size && !check) {
            if (list[index].name == poi.name) check = true
            else index++
        }
        return check
    }
}