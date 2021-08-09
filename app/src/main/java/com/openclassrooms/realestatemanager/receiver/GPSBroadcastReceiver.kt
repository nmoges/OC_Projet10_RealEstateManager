package com.openclassrooms.realestatemanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

class GPSBroadcastReceiver(private val onGpsEventDetected: (Boolean) -> Unit ): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            onGpsEventDetected(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        }
    }
}