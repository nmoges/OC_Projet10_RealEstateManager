package com.openclassrooms.realestatemanager.ui.activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.MediaAccessHandler
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.receiver.NetworkBroadcastReceiver
import com.openclassrooms.realestatemanager.ui.fragments.FragmentEstateDetails
import com.openclassrooms.realestatemanager.ui.fragments.FragmentSettings
import com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate
import com.openclassrooms.realestatemanager.ui.fragments.FragmentNewEstate
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
class MainActivity : AppCompatActivity(), MainActivityCallback {

    /** View Binding parameter */
    private lateinit var binding: ActivityMainBinding

    /** Defines type of layout displayed : */
    var typeLayout: Boolean = false // true: activity_main.xml (large-land)/false: activity_main.xml

    /** Defines type of orientation  */
    var typeOrientation: Boolean = false // true: Orientation landscape/false: Orientation portrait

    /** Id of the container used to display fragments */
    private var containerId: Int = 0

    /** Contains a reference to a [FragmentNewEstate] object */
    private var fragmentNewEstate : Fragment? = null

    /** Contains a reference to a [FragmentEstateDetails] object */
    private var fragmentEstateDetails: Fragment? = null

    /** Contains a reference to a [FragmentSettings] object */
    private var fragmentSettings: Fragment? = null

    /** Contains ViewModel reference */
    lateinit var listEstatesViewModel: ListEstatesViewModel

    private val networkBroadcastReceiver: NetworkBroadcastReceiver = NetworkBroadcastReceiver(this)

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
            fragmentNewEstate = supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG)
            fragmentEstateDetails = supportFragmentManager
                                                       .findFragmentByTag(FragmentEstateDetails.TAG)
            fragmentSettings = supportFragmentManager.findFragmentByTag(FragmentSettings.TAG)
            removeExistingFragments()
            restoreFragments(containerId)
        }
        initializeToolbar()
        handleFloatingActionButton()
        handleConnectivityBarBtnListener()
        listEstatesViewModel = ViewModelProvider(this).get(ListEstatesViewModel::class.java)
        MediaAccessHandler.initializeNbPermissionRequests(this)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkBroadcastReceiver,
                         IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkBroadcastReceiver)
    }

    /**
     * Handles fragment removal operations.
     */
    private fun removeExistingFragments() {
        when {
            fragmentNewEstate != null -> {
                fragmentNewEstate?.let {
                    // TODO : Clear list here
                    supportFragmentManager.beginTransaction().remove(it).commit()
                    supportFragmentManager.executePendingTransactions()
                }
            }
            fragmentEstateDetails != null -> {
                fragmentEstateDetails?.let {
                    supportFragmentManager.beginTransaction().remove(it).commit()
                    supportFragmentManager.executePendingTransactions()
                }
            }
            fragmentSettings != null ->
                fragmentSettings?.let {
                    supportFragmentManager.beginTransaction().remove(it).commit()
                    supportFragmentManager.executePendingTransactions()
                }
        }
    }

    /**
     * Checks the size and orientation screen properties to define the fragment container.
     */
    private fun checkScreenProperties() {
        typeLayout = findViewById<View>(R.id.fragment_container_view) == null
        typeOrientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        containerId = if (typeOrientation && typeLayout) R.id.fragment_container_view_right
                      else R.id.fragment_container_view
    }

    /**
     * Restores fragments display after a configuration change.
     * @param typeContainer : type of container.
     */
    private fun restoreFragments(@IdRes typeContainer: Int) {
        when {
            fragmentNewEstate != null -> {
                launchTransaction(typeContainer, fragmentNewEstate as FragmentNewEstate,
                                                                              FragmentNewEstate.TAG)
            }
            fragmentEstateDetails != null -> {
                launchTransaction(typeContainer, fragmentEstateDetails as FragmentEstateDetails,
                                                                          FragmentEstateDetails.TAG)
            }
            fragmentSettings != null -> {
                launchTransaction(typeContainer, fragmentSettings as FragmentSettings,
                                                                               FragmentSettings.TAG)
            }
        }
    }

    /**
     * Displays [FragmentEstateDetails].
     */
    fun displayFragmentDetails() {
        launchTransaction(containerId, FragmentEstateDetails.newInstance(), FragmentEstateDetails.TAG)
    }

    /**
     * Displays [FragmentSettings]
     */
    fun displayFragmentSettings() {
        launchTransaction(containerId, FragmentSettings.newInstance(), FragmentSettings.TAG)
    }

    /**
     * Displays [FragmentNewEstate].
     * @param updateEstate : defines type of operation will be performed using [FragmentNewEstate] UI
     * (Update of an existing estate, or creation of a new estate)
     */
    fun displayFragmentNewEstate(updateEstate: Boolean) {
        val fragment: FragmentNewEstate = FragmentNewEstate.newInstance()
        fragment.updateEstate = updateEstate
        launchTransaction(containerId, fragment, FragmentNewEstate.TAG)
    }

    /**
     * Initializes [FragmentListEstate] display with the corresponding container.
     */
    private fun initializeFragmentListEstate() {
        // Define container
        val container: Int = if (typeOrientation && typeLayout) R.id.fragment_container_view_left
        else R.id.fragment_container_view
        // Check if existing instance
        if (isFragmentDisplayed(FragmentListEstate.TAG)) {
            val oldFragmentListEstate: FragmentListEstate =
                supportFragmentManager.findFragmentByTag(FragmentListEstate.TAG) as FragmentListEstate
            supportFragmentManager.beginTransaction().remove(oldFragmentListEstate).commitNow()
            supportFragmentManager.popBackStack()
        }
        // Display new instance
        launchTransaction(container, FragmentListEstate.newInstance(), FragmentListEstate.TAG)
    }

    /**
     * Performs fragment transaction by replace current content in [containerId] by
     * given [fragment].
     * @param containerId : id of the fragment container
     * @param fragment : fragment to display
     * @param tag : tag of the associated fragment to display
     */
    private fun launchTransaction(@IdRes containerId: Int, fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
            .commit()
    }

    /**
     * Handles toolbar initialization (support action bar and theme color).
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
     * @param title : toolbar title
     * @param backIconDisplay : display status of the back icon
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
     * Updates connectivity bar display according to network status.
     * @param status : Defines if a network is available or not.
     */
    override fun updateConnectivityBarNetworkDisplay(status: Boolean) {
        if (status) { // Hide bar - - Wifi/Data network active
            val fadeOutAnim: ViewPropertyAnimator = binding.barConnectivityInfo.animate()
                .alpha(0.0f)
                .setDuration(200)
            fadeOutAnim.withEndAction { binding.barConnectivityInfo.visibility = View.GONE}
        }
        else { // Display bar No network active
            binding.barConnectivityInfo.visibility = View.VISIBLE
            ViewCompat.setElevation(binding.barConnectivityInfo, 10F)
            val fadeInAnim: ViewPropertyAnimator = binding.barConnectivityInfo.animate()
                .alpha(1.0f)
                .setDuration(200)
            fadeInAnim.start()
        }
    }

    /**
     * Handles connectivity bar button.
     */
    private fun handleConnectivityBarBtnListener() {
        binding.barConnectivityInfoBtnClose.setOnClickListener {
            updateConnectivityBarNetworkDisplay(true)
        }
    }

    /**
     * Handles click event on Floating Action Button.
     */
    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            listEstatesViewModel.createNewEstate()
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
     * @param visibility : Visibility status value
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
        fragmentNewEstate = supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG)
        fragmentEstateDetails = supportFragmentManager.findFragmentByTag(FragmentEstateDetails.TAG)
        fragmentSettings = supportFragmentManager.findFragmentByTag(FragmentSettings.TAG)
        if (fragmentNewEstate != null || fragmentEstateDetails != null || fragmentSettings != null) {
            // Display FragmentListEstate if needed
            if (!(typeOrientation && typeLayout))
                launchTransaction(containerIdList,
                                  FragmentListEstate.newInstance(), FragmentListEstate.TAG)
            // Reset item selection on list
            val fragmentListEstate = supportFragmentManager.findFragmentByTag(FragmentListEstate.TAG)
            if (fragmentListEstate != null)
                (fragmentListEstate as FragmentListEstate).clearCurrentSelection()
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

    /**
     * Handle floating action button visibility.
     */
    fun handleFabVisibility(visibility: Int) =
        binding.fab.apply { if (visibility == View.VISIBLE) show() else hide() }


    /**
     * Check if a dialog is currently displayed in [FragmentNewEstate].
     */
    private fun checkIfDialogIsDisplayed(): Boolean {
        if (isFragmentDisplayed(FragmentNewEstate.TAG)) {
            val fragment: FragmentNewEstate =
                supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) as FragmentNewEstate
            if (fragment.dismissDialogOnBackPressed()) return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // From camera : Get Uri from saved value in SharedPreferences
        if (resultCode == RESULT_OK && requestCode == AppInfo.REQUEST_IMAGE_CAPTURE) {
            val sharedPreferences: SharedPreferences =
                                getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)
            val uriString: String? = sharedPreferences.getString(AppInfo.PREF_CURRENT_URI, "")
            if (uriString != null) {
                val fragment = supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG)
                               as FragmentNewEstate
                fragment.addNewPhotoUri(uriString.toUri())
            }
        }

        // From Gallery : get Uri from data Intent
        if (resultCode == RESULT_OK && data != null) {
            val imageMediaUri: Uri? = data.data
            if (isFragmentDisplayed(FragmentNewEstate.TAG)) {
                val fragment = supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG)
                               as FragmentNewEstate
                if (imageMediaUri != null) fragment.addNewPhotoUri(imageMediaUri)
            }
        }
    }

    /**
     * Check if main activity child fragment associated with specified [tag] is displayed.
     */
    private fun isFragmentDisplayed(tag: String): Boolean =
        supportFragmentManager.findFragmentByTag(tag) != null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if permissions are all granted
        if (permissions[0] == android.Manifest.permission.READ_EXTERNAL_STORAGE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && permissions[1] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
            && permissions[2] == android.Manifest.permission.CAMERA
            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            val nbRequestsSaved : SharedPreferences = getSharedPreferences(AppInfo.FILE_SHARED_PREF,
                Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = nbRequestsSaved.edit()
            // Reset number of permission requests
            editor.putInt(AppInfo.PREF_PERMISSIONS, 0).apply()
        }
    }
}