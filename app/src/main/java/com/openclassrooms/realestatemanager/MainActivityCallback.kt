package com.openclassrooms.realestatemanager

import androidx.annotation.StringRes

/**
 * Callback interface allowing child fragments to access [MainActivity] methods.
 */
interface MainActivityCallback {
    fun setToolbarTitle(@StringRes title: Int)
}