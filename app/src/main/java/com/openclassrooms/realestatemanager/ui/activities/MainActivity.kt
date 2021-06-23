package com.openclassrooms.realestatemanager.ui.activities

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.fragments.FragmentEstateDetails
import com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate
import com.openclassrooms.realestatemanager.ui.fragments.FragmentNewEstate

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val FAB_STATUS_KEY = "FAB_STATUS_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            restoreViews(savedInstanceState)
            if (supportFragmentManager.findFragmentByTag(FragmentListEstate.TAG) != null) {
                // Remove old FragmentListEstate fragment if exist
                val fragmentListEstate: FragmentListEstate = supportFragmentManager
                    .findFragmentByTag(FragmentListEstate.TAG) as FragmentListEstate
                supportFragmentManager.beginTransaction().remove(fragmentListEstate).commit()
            }
        }

        initializeFragments()
        initializeToolbar()
        handleFloatingActionButton()
    }

    /**
     * Initializes fragments display according to screen orientation
     */
    private fun initializeFragments() {
        cleanBackStack()

        // ORIENTATION_LANDSCAPE
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            selectTransaction(this.window
                .decorView.findViewById<View>(R.id.fragment_container_view) != null)

        // ORIENTATION_PORTRAIT
        else selectTransaction(true)
    }

    /**
     * Selects which fragment transaction to perform according to the type of main activity layout.
     */
    private fun selectTransaction(typeLayout: Boolean) {
        fun addNewFragment(@IdRes typeContainer: Int) {
            // Handle which fragment to display and in which fragment container
            when {
                supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) != null -> {
                    launchTransaction(typeContainer,
                        FragmentNewEstate.newInstance(),
                        FragmentNewEstate.TAG)
                }
                supportFragmentManager.findFragmentByTag(FragmentEstateDetails.TAG) != null -> {
                    launchTransaction(typeContainer,
                        FragmentEstateDetails.newInstance(),
                        FragmentEstateDetails.TAG)
                }
            }
        }

        if (typeLayout) { // activity_main.xml : contains one container
            // Display list of estates
            launchTransaction(R.id.fragment_container_view,
                              FragmentListEstate.newInstance(),
                              FragmentListEstate.TAG)

            // Replace by existing fragment if found with FragmentManger
            addNewFragment(R.id.fragment_container_view)
        }
        else { // activity_main.xml (large-land) : contains two containers
            // Display list of estates on left (FragmentListEstate)
            launchTransaction(R.id.fragment_container_view_left,
                              FragmentListEstate.newInstance(),
                              FragmentListEstate.TAG)

            // Display fragment on right if found with FragmentManager
            addNewFragment(R.id.fragment_container_view_right)
        }
    }

    /**
     * Performs fragment transaction by replace current content in [containerId] by
     * given [fragment]
     */
    fun launchTransaction(@IdRes containerId: Int, fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                              .replace(containerId, fragment, tag)
                              .addToBackStack(null)
                              .commit()
    }

    /**
     * Handles toolbar initialization (support action bar and theme color)
     */
    private fun initializeToolbar() {
        setSupportActionBar(binding.toolbar)
        val color: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                             resources.getColor(R.color.white, null)
                         else resources.getColor(R.color.white)
        binding.toolbar.setTitleTextColor(color)
    }

    /**
     * Handles toolbar update properties (title and back arrow) according to the displayed fragment.
     */
    override fun setToolbarProperties(@StringRes title: Int, backIconDisplay: Boolean) {
        supportActionBar?.apply {
            setTitle(resources.getString(title))
            setDisplayHomeAsUpEnabled(backIconDisplay)
            if (backIconDisplay)
                setHomeAsUpIndicator(ResourcesCompat
                    .getDrawable(resources, R.drawable.ic_baseline_arrow_back_24dp_white, null))
        }
    }

    /**
     * Handles click event on Floating Action Button.
     */
    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (this.window.decorView.findViewById<View>(R.id.fragment_container_view) != null)
                    launchTransaction(R.id.fragment_container_view,
                                      FragmentNewEstate.newInstance(),
                                      FragmentNewEstate.TAG)
                else launchTransaction(R.id.fragment_container_view_right,
                                       FragmentNewEstate.newInstance(),
                                       FragmentNewEstate.TAG)
            }
            else launchTransaction(R.id.fragment_container_view,
                                  FragmentNewEstate.newInstance(),
                                  FragmentNewEstate.TAG)
            binding.fab.hide()
            handleBackgroundGridVisibility(View.INVISIBLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.fab.let { outState.putInt(FAB_STATUS_KEY, it.visibility) } // Save state
    }

    /**
     * Restore all [MainActivity] views states after a configuration change.
     * @param savedInstanceState: [Bundle]
     */
    private fun restoreViews(savedInstanceState: Bundle) {
        val visibility: Int = savedInstanceState.getInt(FAB_STATUS_KEY)
        if (visibility == View.VISIBLE) binding.fab.show()
        else binding.fab.hide()
    }

    /**
     * Handles visibility of background MaterialText and ImageView for "large-land"
     * activity_main.xml only
     */
    private fun handleBackgroundGridVisibility(visibility: Int) {
        binding.apply {
            imgBackground?.visibility = visibility
            txtBackground?.visibility = visibility
        }
    }

    /**
     * Cleans back stack before initializing fragment containers.
     * @return : true if an existing fragment has been popped off the back stack, else false.
     */
    private fun cleanBackStack(): Boolean {
        if (supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) != null ||
            supportFragmentManager.findFragmentByTag(FragmentEstateDetails.TAG) != null) {
            supportFragmentManager.popBackStack()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        // Close application if no additional fragment to remove from back stack
        if (!cleanBackStack()) finishAffinity()
        else {
            binding.fab.show()
            handleBackgroundGridVisibility(View.VISIBLE)
            setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)
        }
    }
}