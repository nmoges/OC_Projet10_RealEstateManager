package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.text.Editable
import androidx.annotation.StringRes

object StringHandler {

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

    fun convertStringToEditable(text: String): Editable =
        Editable.Factory.getInstance().newEditable(text)
}