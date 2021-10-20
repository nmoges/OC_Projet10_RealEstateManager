package com.openclassrooms.realestatemanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.ui.activities.MainActivityCallback

/**
 * [BroadcastReceiver] used to detect any network event.
 */
class NetworkBroadcastReceiver(private val callback: MainActivityCallback): BroadcastReceiver() {

    /**
     * Handles Network events.
     * @param context : Context
     * @param intent : Intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        callback.apply {
            updateConnectivityBarNetworkDisplay(Utils.isInternetAvailable(context))
            if (!isInitialStickyBroadcast)
                if (Utils.isInternetAvailable(context)) updateURIsPhotosInDB()
        }
    }
}