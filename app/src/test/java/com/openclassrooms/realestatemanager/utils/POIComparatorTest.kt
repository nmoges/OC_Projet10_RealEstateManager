package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.data.model.PointOfInterest
import com.openclassrooms.realestatemanager.utils.poi.POIComparator
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class POIComparatorTest {

    /**
     * Tests [POIComparator] method "containsPOI().
     */
    @Test
    fun test_contains_POI() {
        val pointOfInterest1 = PointOfInterest(0, "Bar")
        val pointOfInterest2 = PointOfInterest(0, "Museum")
        val listPOI = mutableListOf(
            PointOfInterest(0, "Bar"),
            PointOfInterest(1, "Market"),
            PointOfInterest(2, "Restaurant"),
            PointOfInterest(3, "School")
        )
        assertEquals(true, POIComparator.containsPOI(pointOfInterest1, listPOI))
        assertEquals(false, POIComparator.containsPOI(pointOfInterest2, listPOI))
    }
}