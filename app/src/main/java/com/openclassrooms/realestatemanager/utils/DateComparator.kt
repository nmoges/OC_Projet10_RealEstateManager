package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.Utils
import java.text.ParseException
import java.util.*

/**
 * Object defining a set of functions to handle dates comparison.
 */
object DateComparator {

    /**
     * Compares two dates.
     * @param startDate : start date
     * @param endDate : end date
     */
    fun compareDates(startDate: String, endDate: String): Boolean {
        try {
            val startTime = Calendar.getInstance()
            startTime.time = Utils.convertFormatToDate(startDate)
            val endTime = Calendar.getInstance()
            endTime.time = Utils.convertFormatToDate(endDate)
            return when {
                startTime.get(Calendar.YEAR) > endTime.get(Calendar.YEAR) -> { false }
                startTime.get(Calendar.YEAR) < endTime.get(Calendar.YEAR) -> { true }
                else -> {
                    when {
                        startTime.get(Calendar.MONTH) > endTime.get(Calendar.MONTH) -> false
                        startTime.get(Calendar.MONTH) < endTime.get(Calendar.MONTH) -> true
                        else -> startTime.get(Calendar.DAY_OF_MONTH) <= endTime.get(Calendar.DAY_OF_MONTH)
                    }

                }
            }
        } catch (exception: ParseException) { exception.printStackTrace() }
        return false
    }
}