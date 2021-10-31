package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInstruTest {

    @get: Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Tests if network detection works. This test must be launched with an active internet connection.
     */
    @Test
    fun test_internet_availability() {
        activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityScenarioRule.scenario.onActivity {
            assertEquals(true, Utils.isInternetAvailable(context))
        }
    }
}