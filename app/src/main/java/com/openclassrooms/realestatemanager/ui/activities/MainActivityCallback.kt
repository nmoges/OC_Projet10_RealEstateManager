package com.openclassrooms.realestatemanager.ui.activities

import androidx.annotation.StringRes

/**
 * Callback interface allowing child fragments to access [MainActivity] methods.
 */
interface MainActivityCallback {
    fun setToolbarProperties(@StringRes title: Int, backIconDisplay: Boolean)
}