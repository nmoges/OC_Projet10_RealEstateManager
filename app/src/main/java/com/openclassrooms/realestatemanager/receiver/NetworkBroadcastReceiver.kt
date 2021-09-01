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

    override fun onReceive(context: Context?, intent: Intent?) {
        callback.updateConnectivityBarNetworkDisplay(Utils.isInternetAvailable(context));
    }
}