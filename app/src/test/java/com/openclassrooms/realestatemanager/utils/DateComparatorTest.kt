package com.openclassrooms.realestatemanager.utils

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import junit.framework.Assert.assertEquals

@RunWith(JUnit4::class)
class DateComparatorTest {

    /**
     * Tests [DateComparator] method "compareDates()"
     */
    @Test
    fun test_date_comparator_object() {
        assertEquals(true, DateComparator.compareDates("22/01/2021", "12/11/2022"))
        assertEquals(false, DateComparator.compareDates("22/01/2021", "20/01/2021"))
        assertEquals(true, DateComparator.compareDates("22/01/2021", "22/01/2021"))
    }
}