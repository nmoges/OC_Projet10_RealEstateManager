package com.openclassrooms.realestatemanager.utils

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

/**
 * Object defining a progress bar provider.
 */
object ProgressBarHandler {

    /**
     * Provides a [CircularProgressDrawable] to display while loading view operations.
     * @param context : [Context] of the view
     * @return : a [CircularProgressDrawable] object
     */
    fun getProgressBarDrawable(context: Context) : CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
    }
}