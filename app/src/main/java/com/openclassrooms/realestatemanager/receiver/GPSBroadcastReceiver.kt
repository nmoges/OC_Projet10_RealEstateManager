package com.openclassrooms.realestatemanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.openclassrooms.realestatemanager.utils.GPSAccessHandler

/**
 * [BroadcastReceiver] used to detect any GPS event.
 */
class GPSBroadcastReceiver(private val onGpsEventDetected: (Boolean) -> Unit ): BroadcastReceiver() {

    /**
     * Handles GPS events.
     * @param context : Context
     * @param intent : Intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            onGpsEventDetected(GPSAccessHandler.isGPSEnabled(locationManager))
        }
    }
}