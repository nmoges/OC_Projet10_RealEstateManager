package com.openclassrooms.realestatemanager

import android.app.Notification
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.notification.NotificationHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test file for [NotificationHandler] class.
 */
@RunWith(AndroidJUnit4::class)
class NotificationHandlerTest {

    lateinit var context: Context
    lateinit var notificationHandler: NotificationHandler
    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        notificationHandler = NotificationHandler(context)
    }

    /**
     * Tests if notification priority value is correctly returned by [NotificationHandler].
     */
    @Suppress("DEPRECATION")
    @Test
    fun test_get_notification_priority() {
        val priority = notificationHandler.getNotificationPriority()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            assertEquals(Notification.PRIORITY_DEFAULT, priority)
        else
            assertEquals(NotificationCompat.PRIORITY_DEFAULT, priority)
    }

    /**
     * Tests if notification channel is correctly returned by [NotificationHandler].
     */
    @Test
    fun test_notification_channel_creation() {
        notificationHandler.createChannel()
        assertNotNull(notificationHandler.manager.getNotificationChannel(AppInfo.CHANNEL_ID))
        assertEquals(AppInfo.CHANNEL_DESCRIPTION, notificationHandler.manager
                                            .getNotificationChannel(AppInfo.CHANNEL_ID).description)
        assertEquals(AppInfo.CHANNEL_NAME, notificationHandler.manager
                                            .getNotificationChannel(AppInfo.CHANNEL_ID).name)
    }
}