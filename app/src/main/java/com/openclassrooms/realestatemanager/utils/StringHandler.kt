package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.text.Editable
import androidx.annotation.StringRes

/**
 * Object defining a set of functions to String objects.
 */
object StringHandler {

    /**
     * Gets string to display in a slider.
     */
    fun getSliderString(maxValue: Int,
                        currentValue: Int,
                        @StringRes resMaxValue: Int,
                        @StringRes resValue: Int?,
                        type: Boolean,
                        context: Context?): String? {
        return if (currentValue == maxValue)
            context?.resources?.getString(resMaxValue, currentValue)
        else
            if (type && resValue != null) context?.resources?.getString(resValue, currentValue)
            else currentValue.toString()
    }

    /**
     * Converts a [String] object to [Editable].
     */
    fun convertStringToEditable(text: String): Editable =
        Editable.Factory.getInstance().newEditable(text)
}