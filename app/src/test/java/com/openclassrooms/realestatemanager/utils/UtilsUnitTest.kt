package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.Utils
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

/**
 * Test file for [Utils] file.
 */
@RunWith(JUnit4::class)
class UtilsUnitTest {

    /**
     * Tests [Utils] method "convertDollarToEuro()".
     */
    @Test
    fun test_convert_dollars_to_euros() {
        assertEquals(81, Utils.convertDollarToEuro(100))
        assertEquals(122, Utils.convertDollarToEuro(150))
        assertEquals(205, Utils.convertDollarToEuro(253))
    }

    /**
     * Tests [Utils] method "convertEuroToDollar()".
     */
    @Test
    fun test_convert_euros_to_dollars() {
        assertEquals(100, Utils.convertEuroToDollar(81))
        assertEquals(150, Utils.convertEuroToDollar(122))
        assertEquals(252, Utils.convertEuroToDollar(205))
    }

    /**
     * Tests [Utils] method "getDate()"
     */
    @Test
    fun test_get_date() {
        val calendar = Calendar.getInstance()
        val date = Utils.getDate()
        val day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) "0${calendar.get(Calendar.DAY_OF_MONTH)}"
                  else "${calendar.get(Calendar.DAY_OF_MONTH)}"
        val month = if (calendar.get(Calendar.MONTH)+1 < 10) "0${calendar.get(Calendar.MONTH)+1}"
        else "${calendar.get(Calendar.MONTH)+1}"
        val year = "${calendar.get(Calendar.YEAR)}"

        assertEquals(year, date.substring(0,4))
        assertEquals(month, date.substring(5,7))
        assertEquals(day, date.substring(8,10))
    }

    /**
     * Test [Utils] method "convertDateToFormat()"
     */
    @Test
    fun test_convert_date_to_format() {
        val date = Calendar.getInstance()
        date.set(2021, 10, 6)
        val language = Locale.getDefault().language
        if (language == "en")
            assertEquals("11/06/2021", Utils.convertDateToFormat(date.time))
        else
            assertEquals("06/11/2021", Utils.convertDateToFormat(date.time))
    }

    /**
     * Tests [Utils] method "convertFormatToDate()".
     */
    @Test
    fun test_convert_format_to_date() {
        val calendar = Calendar.getInstance()
        val language = Locale.getDefault().language
        if (language == "en")
            calendar.time = Utils.convertFormatToDate("10/04/2021")
        else
            calendar.time = Utils.convertFormatToDate("04/10/2021")
        assertEquals(10, calendar.get(Calendar.MONTH)+1)
        assertEquals(4, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(2021, calendar.get(Calendar.YEAR))
    }

    /**
     * Tests [Utils] method "convertStringToSQLiteFormat()".
     */
    @Test
    fun test_convert_string_to_SQLite_format() {
        val convertedString: String
        val language = Locale.getDefault().language
        if (language == "en") {
            convertedString = Utils.convertStringToSQLiteFormat("10/22/2021")
            assertEquals("2021-10-22", convertedString)
        }
        else {
            convertedString = Utils.convertStringToSQLiteFormat("22/10/2021")
            assertEquals("2021-10-22", convertedString)
        }
    }
}