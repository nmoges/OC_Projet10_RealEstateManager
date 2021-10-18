package com.openclassrooms.realestatemanager

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.data.service.AutocompleteService
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutocompleteServiceTest {

    @get: Rule var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Tests if auto-generated activity for autocomplete requests is correctly launched.
     */
    @Test
    fun test_autocomplete_activity_display() {
        activityScenarioRule.scenario.onActivity { activity ->
            AutocompleteService.performAutocompleteRequest(activity)
        }
    }
}