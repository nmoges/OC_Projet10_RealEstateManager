package com.openclassrooms.realestatemanager.ui.activities

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.fragments.FragmentEstateDetails
import com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate
import com.openclassrooms.realestatemanager.ui.fragments.FragmentNewEstate
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
class MainActivity : AppCompatActivity(), MainActivityCallback {

    // Binding
    private lateinit var binding: ActivityMainBinding

    // Screen parameters
    var typeLayout: Boolean = false // true : activity_main.xml (large-land)
                                            // false : activity_main.xml
    var typeOrientation: Boolean = false // true : Orientation landscape
                                                // false : Orientation portrait
    private var containerId: Int = 0

    // Fragments
    private var fragmentNewEstateStack : Fragment? = null
    private var fragmentEstateDetailsStack: Fragment? = null

    lateinit var listEstatesViewModel: ListEstatesViewModel

    companion object {
        const val FAB_STATUS_KEY = "FAB_STATUS_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkScreenProperties()

        initializeFragmentListEstate()
        if (savedInstanceState != null) {
            restoreViews(savedInstanceState)
            fragmentNewEstateStack = supportFragmentManager
                                                    .findFragmentByTag(FragmentNewEstate.TAG)
            fragmentEstateDetailsStack = supportFragmentManager
                                                    .findFragmentByTag(FragmentEstateDetails.TAG)
            removeExistingFragments()
            restoreFragments(containerId)
        }
        initializeToolbar()
        handleFloatingActionButton()

        listEstatesViewModel = ViewModelProvider(this).get(ListEstatesViewModel::class.java)
    }

    private fun removeExistingFragments() {
        when {
            fragmentNewEstateStack != null -> {
                fragmentNewEstateStack?.let {
                    supportFragmentManager.beginTransaction().remove(it).commit()
                    supportFragmentManager.executePendingTransactions()
                }
            }
            fragmentEstateDetailsStack != null -> {
                fragmentEstateDetailsStack?.let {
                    supportFragmentManager.beginTransaction().remove(it).commit()
                    supportFragmentManager.executePendingTransactions()
                }
            }
        }
    }

    private fun checkScreenProperties() {
        typeLayout = findViewById<View>(R.id.fragment_container_view) == null
        typeOrientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        containerId = if (typeOrientation && typeLayout) R.id.fragment_container_view_right
                      else R.id.fragment_container_view
    }

    private fun restoreFragments(@IdRes typeContainer: Int) {
        // Handle which fragment to display and in which fragment container
        when {
            fragmentNewEstateStack != null -> {
                launchTransaction(typeContainer,
                    fragmentNewEstateStack as FragmentNewEstate,
                    FragmentNewEstate.TAG)
            }
            fragmentEstateDetailsStack != null -> {
                launchTransaction(typeContainer,
                    fragmentEstateDetailsStack as FragmentEstateDetails,
                    FragmentEstateDetails.TAG)
            }
        }
    }

    fun displayFragmentDetails() {
        launchTransaction(containerId, FragmentEstateDetails.newInstance(), FragmentEstateDetails.TAG)
    }

    fun displayFragmentNewEstate(updateEstate: Boolean) {
        val fragment: FragmentNewEstate = FragmentNewEstate.newInstance()
        fragment.updateEstate = updateEstate
        launchTransaction(containerId, fragment, FragmentNewEstate.TAG)
    }

    private fun initializeFragmentListEstate() {
        // Define container
        val container: Int = if (typeOrientation && typeLayout) R.id.fragment_container_view_left
        else R.id.fragment_container_view

        // Check if existing instance
        if (supportFragmentManager.findFragmentByTag(FragmentListEstate.TAG) != null) {
            val oldFragmentListEstate: FragmentListEstate =
                supportFragmentManager.findFragmentByTag(FragmentListEstate.TAG) as FragmentListEstate
            supportFragmentManager.beginTransaction().remove(oldFragmentListEstate).commit()
            supportFragmentManager.popBackStack()
        }

        // Display new instance
        launchTransaction(container, FragmentListEstate.newInstance(), FragmentListEstate.TAG)
    }

    /**
     * Performs fragment transaction by replace current content in [containerId] by
     * given [fragment]
     */
    private fun launchTransaction(@IdRes containerId: Int, fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
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
            launchTransaction(containerId, FragmentNewEstate.newInstance(), FragmentNewEstate.TAG)

            handleFabVisibility(View.INVISIBLE)
            handleBackgroundGridVisibility(View.INVISIBLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.fab.let { outState.putInt(FAB_STATUS_KEY, it.visibility) }
    }

    /**
     * Restore all [MainActivity] views states after a configuration change.
     * @param savedInstanceState: [Bundle]
     */
    private fun restoreViews(savedInstanceState: Bundle) {
        handleFabVisibility(savedInstanceState.getInt(FAB_STATUS_KEY))
        if (typeOrientation && typeLayout) handleBackgroundGridVisibility(binding.fab.visibility)
    }

    /**
     * Handles visibility of background MaterialText and ImageView for "large-land"
     * activity_main.xml only
     */
    fun handleBackgroundGridVisibility(visibility: Int) {
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
        val containerIdList: Int = if (typeOrientation && typeLayout) R.id.fragment_container_view_left
        else R.id.fragment_container_view

        fragmentNewEstateStack = supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG)
        fragmentEstateDetailsStack = supportFragmentManager.findFragmentByTag(FragmentEstateDetails.TAG)

        if (fragmentNewEstateStack != null || fragmentEstateDetailsStack != null) {
            // Display FragmentListEstate if needed
            if (!(typeOrientation && typeLayout)) {
                launchTransaction(containerIdList, FragmentListEstate.newInstance(), FragmentListEstate.TAG)
            }

            // Reset item selection on list
            val fragmentListEstate = supportFragmentManager.findFragmentByTag(FragmentListEstate.TAG)
            if (fragmentListEstate != null) {
                (fragmentListEstate as FragmentListEstate).clearCurrentSelection()
            }

            removeExistingFragments()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        if (!checkIfDialogIsDisplayed()) {
            if (!cleanBackStack()) finishAffinity()
            else {
                handleFabVisibility(View.VISIBLE)
                handleBackgroundGridVisibility(View.VISIBLE)
                setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)
            }
        }
    }

    fun handleFabVisibility(visibility: Int) {
        binding.fab.apply {
            if (visibility == View.VISIBLE) show() else hide()
        }
    }

    private fun checkIfDialogIsDisplayed(): Boolean {
        if (supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) != null) {
            val fragment: FragmentNewEstate =
                supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) as FragmentNewEstate
            if (fragment.dismissDialogOnBackPressed()) return true
        }
        return false
    }
}