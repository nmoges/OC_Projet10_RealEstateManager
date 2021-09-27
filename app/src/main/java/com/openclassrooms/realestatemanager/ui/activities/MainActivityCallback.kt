package com.openclassrooms.realestatemanager.ui.activities

import androidx.annotation.StringRes

/**
 * Callback interface allowing child fragments to access [MainActivity] methods.
 */
interface MainActivityCallback {

    /**
     * Handles toolbar updates.
     * @param title : Title to display
     * @param backIconDisplay : Back icon status
     */
    fun setToolbarProperties(@StringRes title: Int, backIconDisplay: Boolean)

    /**
     * Handles network bar updates.
     * @param status : network bar display status
     */
    fun updateConnectivityBarNetworkDisplay(status: Boolean)

    /**
     * Callback for SQLite Database photos URI updates.
     */
    fun updateURIsPhotosInDB()
}