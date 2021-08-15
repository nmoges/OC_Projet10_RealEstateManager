package com.openclassrooms.realestatemanager.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.activities.MainActivity


object GPSAccessHandler {
    /** [SharedPreferences] file */
    private lateinit var filePreferences: SharedPreferences

    /** [SharedPreferences] file editor */
    private lateinit var editor: SharedPreferences.Editor

    /** Contains the number of permission requests sent by user */
    private var nbRequest: Int = 0

    private val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    /**
     * Initializes [nbRequest] using [SharedPreferences].
     * @param activity : Main activity
     */
    fun initializeNbPermissionRequests(activity: MainActivity) {
        filePreferences = activity.getSharedPreferences(
            AppInfo.FILE_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        // Get the number of permission requests already sent
        nbRequest = filePreferences.getInt(AppInfo.PREF_PERMISSION_LOCATION, 0)
        editor = filePreferences.edit()
    }

    /**
     * Checks if requested location permission is granted.
     * @param activity : Main activity
     */
    fun checkLocationPermission(activity: MainActivity): Boolean = ContextCompat.checkSelfPermission(
        activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /**
     * Requests location permission to the OS.
     * @param activity : Main activity
     */
    fun requestPermissionLocation(activity: MainActivity) {
        if (nbRequest <= 1) { // First request
            ActivityCompat.requestPermissions(activity,
                permission,
                AppInfo.REQUEST_PERMISSIONS_CODE)
        }
        else { // Don't ask again checked
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
                displayAccessLocationDialog(activity)
            }
            else {
                ActivityCompat.requestPermissions(activity, permission,
                    AppInfo.REQUEST_PERMISSIONS_CODE
                )
            }
        }
        nbRequest++
        editor.putInt(AppInfo.PREF_PERMISSION_LOCATION, nbRequest).apply()
    }

    /**
     * Displays an educational UI for the location permission.
     * @param activity : Main activity
     */
    private fun displayAccessLocationDialog(activity: MainActivity) {
        val builderAccessLocationDialog = AlertDialog.Builder(activity)
            .setTitle(activity.resources.getString(R.string.str_dialog_loc_permission_access_title))
            .setMessage(activity.resources.getString(R.string.str_dialog_loc_permission_access_message))
            .setPositiveButton(activity.resources
                .getString(R.string.str_dialog_permission_access_button_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse(activity.resources.getString(R.string.uri_string_package))
                activity.startActivity(intent)
            }
            .setNegativeButton(activity.resources.getString(R.string.str_dialog_button_cancel)) { _, _ -> }
            .create()
        builderAccessLocationDialog.show()
    }

    fun checkDistanceEstateFromGPSLocation(estateLatLng: LatLng, gpsPosition: LatLng): Boolean {
        val result = FloatArray(1)
        Location.distanceBetween(gpsPosition.latitude,
                                 gpsPosition.longitude,
                                 estateLatLng.latitude,
                                 estateLatLng.longitude,
                                 result)

        // Display estates which distance is < 1000m from user location
        if (result[0] < 1000) return true
        return false
    }

    fun isGPSEnabled(locationManager: LocationManager): Boolean =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
