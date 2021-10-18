package com.openclassrooms.realestatemanager

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.ui.fragments.FragmentMap
import com.openclassrooms.realestatemanager.ui.fragments.FragmentNewEstate
import com.openclassrooms.realestatemanager.ui.fragments.FragmentSearch
import com.openclassrooms.realestatemanager.ui.fragments.FragmentSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test file for fragments display.
 */
@RunWith(AndroidJUnit4::class)
class FragmentsTest {
    @get: Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    lateinit var context: Context

    @Before
    fun setUp() {
        val scenario = activityScenarioRule.scenario
        scenario.moveToState(Lifecycle.State.RESUMED)
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Tests if [FragmentNewEstate] is correctly displayed when called.
     */
    @Test
    fun test_display_new_estate_fragment() {
        onView(withId(R.id.fab)).perform(click())
        activityScenarioRule.scenario.onActivity { activity ->
             assertNotNull(activity.isFragmentDisplayed(AppInfo.TAG_FRAGMENT_NEW_ESTATE))
        }
    }

    /**
     * Tests if [FragmentMap] is correctly displayed when called.
     */
    @Test
    fun test_display_map_fragment() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Map"
        onView(withText(R.string.str_item_map)).perform(click())
        // Check if FragmentMap is displayed
        activityScenarioRule.scenario.onActivity { activity ->
            assertNotNull(activity.isFragmentDisplayed(AppInfo.TAG_FRAGMENT_MAP))
        }
    }

    /**
     * Tests if [FragmentSettings] is correctly displayed when called.
     */
    @Test
    fun test_display_fragment_settings() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Settings"
        onView(withText(R.string.str_item_settings)).perform(click())
        // Check if FragmentSettings is displayed
        activityScenarioRule.scenario.onActivity { activity ->
            assertNotNull(activity.isFragmentDisplayed(AppInfo.TAG_FRAGMENT_SETTINGS))
        }
    }

    /**
     * Tests if [FragmentSearch] is correctly displayed when called.
     */
    @Test
    fun test_display_search_fragment() {
        // Click on "Search" item
        try {
            onView(withId(R.id.search)).perform(click())
        } catch (exception: NoMatchingViewException) {
            print(exception)
            openActionBarOverflowOrOptionsMenu(context)
            onView(withText(R.string.str_item_search))
        }
        // Check if FragmentSearch is displayed
        activityScenarioRule.scenario.onActivity { activity ->
            assertNotNull(activity.isFragmentDisplayed(AppInfo.TAG_FRAGMENT_SEARCH))
        }
    }

    /**
     * Tests if "Add new agent" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_add_agent_dialog() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Add agent"
        onView(withText(R.string.str_item_add_agent)).perform(click())
        // Check if dialog is displayed
        onView(withText(R.string.str_dialog_add_agent_title)).check(matches(isDisplayed()))
    }

    /**
     * Tests if "Logout" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_logout_dialog() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Logout"
        onView(withText(R.string.str_item_logout)).perform(click())
        // Check if dialog is displayed
        onView(withText(R.string.str_dialog_logout_title)).check(matches(isDisplayed()))
    }

    /**
     * Tests if "Reset estate" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_reset_estate_dialog() {
        // Display FragmentNewEstate
        onView(withId(R.id.fab)).perform(click())
        // Display dialog
        onView(withId(R.id.reset)).perform(click())
    }

    /**
     * Tests if "Reset search" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_reset_search_dialog() {
        // Display FragmentSearch
        try {
            onView(withId(R.id.search)).perform(click())
        } catch (exception: NoMatchingViewException) {
            print(exception)
            openActionBarOverflowOrOptionsMenu(context)
            onView(withText(R.string.str_item_search))
        }
        // Click on "reset search" item
        onView(withId(R.id.reset)).perform(click())
    }

    /**
     * Tests if "Select filters" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_search_filters_dialog() {
        // Display FragmentSearch
        try {
            onView(withId(R.id.search)).perform(click())
        } catch (exception: NoMatchingViewException) {
            print(exception)
            openActionBarOverflowOrOptionsMenu(context)
            onView(withText(R.string.str_item_search))
        }
        // Click on "filter" item
        onView(withId(R.id.filter)).perform(click())
    }

    /**
     * Tests if "Cancel estate" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_cancel_estate_creation_dialog() {
        // Display FragmentNewEstate
        onView(withId(R.id.fab)).perform(click())
        // Display dialog
        Espresso.pressBack()
    }

    /**
     * Tests if "Delete account" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_delete_account_dialog() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Settings"
        onView(withText(R.string.str_item_settings)).perform(click())
        // Click on "Delete account" section
        onView(withId(R.id.card_view_delete_account)).perform(click())
    }

    /**
     * Tests if "Select currency" dialog is correctly displayed when called.
     */
    @Test
    fun test_display_select_currency_dialog() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Settings"
        onView(withText(R.string.str_item_settings)).perform(click())
        // Click on "Select currency" section
        onView(withId(R.id.card_view_currency)).perform(click())
    }

    /**
     * Tests if changing currency is correctly saved in SharedPreferences file.
     */
    @Test
    fun test_change_currency_in_settings() {
        // Open toolbar menu
        openActionBarOverflowOrOptionsMenu(context)
        // Click on item "Settings"
        onView(withText(R.string.str_item_settings)).perform(click())
        // Click on "Select currency" section
        onView(withId(R.id.card_view_currency)).perform(click())
        // Click on "US
        onView(withId(R.id.constraint_layout_view_euro_currency)).perform(click())
        activityScenarioRule.scenario.onActivity { activity ->
            val filePreferences: SharedPreferences =
                activity.getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)
            val currency = filePreferences.getString(AppInfo.PREF_CURRENCY, "USD")
            assertEquals("EUR", currency)
        }
    }
}