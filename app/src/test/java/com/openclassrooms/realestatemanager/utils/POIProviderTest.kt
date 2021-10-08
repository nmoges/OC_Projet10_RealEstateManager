package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.utils.poi.POIProvider
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class POIProviderTest {

    /**
     * Tests [POIProvider] method "providePointOfInterest().
     */
    @Test
    fun test_point_of_interest_provider() {
        assertEquals("Bar", POIProvider.providePointOfInterest(0))
        assertEquals("Library", POIProvider.providePointOfInterest(2))
        assertEquals("Market", POIProvider.providePointOfInterest(3))
        assertEquals("School", POIProvider.providePointOfInterest(7))
    }

    /**
     * Tests [POIProvider] method "provideIndexFromPointOfInterest().
     */
    @Test
    fun test_index_provider() {
        assertEquals(0, POIProvider.provideIndexFromPointOfInterest("Bar"))
        assertEquals(2, POIProvider.provideIndexFromPointOfInterest("Library"))
        assertEquals(3, POIProvider.provideIndexFromPointOfInterest("Market"))
        assertEquals(7, POIProvider.provideIndexFromPointOfInterest("School"))
    }
}