package com.openclassrooms.realestatemanager.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes

/**
 * Object defining a layoutInflater provider.
 */
object LayoutInflaterProvider {

    /**
     * Inflates a XML layout using a [LayoutInflater].
     * @param layout : Layout to instantiate
     * @param context : context
     * @return : view (inflated layout)
     */
    fun getViewFromLayoutInflater(@LayoutRes layout: Int, context: Context?): View? {
        val inflater: LayoutInflater? =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater
        return inflater?.inflate(layout, null)
    }
}