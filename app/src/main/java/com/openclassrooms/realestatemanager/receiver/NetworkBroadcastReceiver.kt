package com.openclassrooms.realestatemanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import com.openclassrooms.realestatemanager.ui.activities.MainActivityCallback

/**
 * [BroadcastReceiver] used to detect any network event.
 */
class NetworkBroadcastReceiver(private val callback: MainActivityCallback): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager: ConnectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        callback.updateConnectivityBarNetworkDisplay(isNetworkAvailable(connectivityManager))

    }

    /**
     * Checks if any network is available
     * @param connectivityManager : ConnectivityManager
     * @return Boolean : connectivity status
     */
    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork != null
        } else {
            connectivityManager.activeNetworkInfo != null
        }
    }
}