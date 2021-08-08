package com.openclassrooms.realestatemanager.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R

/**
 * Notification provider.
 */
class NotificationHandler(val context: Context) {

    private val manager: NotificationManager =
                       context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Builds a notification.
     */
    fun createNotification(type: Boolean) {
        val message = if (type) context.resources.getString(R.string.notification_message_estate_updated)
                      else context.resources.getString(R.string.notification_message_estate_available)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, AppInfo.CHANNEL_ID)
                              .setSmallIcon(R.drawable.ic_launcher_background)
                              .setContentTitle(context.resources.getString(R.string.notification_title))
                              .setContentText(message)
                              .setAutoCancel(true)
                              .setPriority(setPriority())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()

        manager.notify( 0, builder.build())
    }

    /**
     * Defines notification priority.
     */
    private fun setPriority(): Int {
        return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    Notification.PRIORITY_DEFAULT
                else NotificationCompat.PRIORITY_DEFAULT

    }

    /**
     * Defines notification channel.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(AppInfo.CHANNEL_ID,
                                          AppInfo.CHANNEL_NAME,
                                          NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "description"
        manager.createNotificationChannel(channel)
    }
}