package com.openclassrooms.realestatemanager.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.authentication.AuthenticationFirebase
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.data.model.Agent
import com.openclassrooms.realestatemanager.notification.NotificationHandler
import com.openclassrooms.realestatemanager.receiver.NetworkBroadcastReceiver
import com.openclassrooms.realestatemanager.ui.fragments.*
import com.openclassrooms.realestatemanager.utils.CustomTextWatcher
import com.openclassrooms.realestatemanager.utils.GPSAccessHandler
import com.openclassrooms.realestatemanager.utils.MediaAccessHandler
import com.openclassrooms.realestatemanager.utils.StringHandler
import com.openclassrooms.realestatemanager.viewmodels.EstateViewModel
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import com.openclassrooms.realestatemanager.viewmodels.ListTagsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityCallback {

    /** View Binding parameter */
    private lateinit var binding: ActivityMainBinding

    /** Defines type of layout displayed : */
    var typeLayout: Boolean = false // true: activity_main.xml (large-land)/false: activity_main.xml

    /** Defines type of orientation  */
    var typeOrientation: Boolean = false // true: Orientation landscape/false: Orientation portrait

    /** Id of the container used to display fragments */
    private var containerId: Int = 0

    /** Id of the container used to display list/search fragments */
    private var containerIdLists: Int = 0

    /** Contains reference to [ListEstatesViewModel] */
    lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains reference to [ListTagsFragmentViewModel] */
    lateinit var listFragmentsViewModel: ListTagsFragmentViewModel

    /** Contains reference to [EstateViewModel] */
    lateinit var estatesViewModel: EstateViewModel

    /** Contains a reference to a [NetworkBroadcastReceiver] object */
    private val networkBroadcastReceiver: NetworkBroadcastReceiver = NetworkBroadcastReceiver(this)

    /** Contain a [NotificationHandler] object reference */
    lateinit var notificationHandler: NotificationHandler

    /** Location provider */
    lateinit var locationProviderClient: FusedLocationProviderClient

    /** Defines an [AlertDialog] allowing user to enter a new agent */
    private lateinit var builderAddAgentDialog: AlertDialog

    /** Contains fields temporary value for an [Agent] object */
    private var firstNameAgent = ""
    private var lastNameAgent = ""

    /** Defines an [AlertDialog] for logout */
    private lateinit var builderLogoutDialog: AlertDialog

    /** Status of the network bar */
    private var networkBarDisplayStatus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listEstatesViewModel = ViewModelProvider(this)[ListEstatesViewModel::class.java]
        listFragmentsViewModel = ViewModelProvider(this)[ListTagsFragmentViewModel::class.java]
        estatesViewModel = ViewModelProvider(this)[EstateViewModel::class.java]
        checkScreenProperties()
        initializeDialogAddAgent()
        initializeDialogLogout()
        if (savedInstanceState != null) {
            deleteOldFragments()
            restoreFragmentsFromBackStack()
            restoreDialogs(savedInstanceState)
            networkBarDisplayStatus = savedInstanceState.getBoolean(AppInfo.NETWORK_BAR_STATUS_KEY)
        }
        initializeToolbar()
        initializeNotificationHandler()
        handleConnectivityBarBtnListener()
        MediaAccessHandler.initializeNbPermissionRequests(this)
        GPSAccessHandler.initializeNbPermissionRequests(this)
        initializeMapClient()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkBroadcastReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        updateActivityViewsIfBackStackIsEmpty()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkBroadcastReceiver)
    }

    /**
     * Initialize map client.
     */
    private fun initializeMapClient() {
        if (!Places.isInitialized()) Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        Places.createClient(this)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)

    }

    /**
     * Initializes a [NotificationHandler] object.
     */
    private fun initializeNotificationHandler() {
        notificationHandler = NotificationHandler(this)
    }

    /**
     * Checks the size and orientation screen properties to define the fragment container.
     */
    private fun checkScreenProperties() {
        typeLayout = findViewById<View>(R.id.fragment_container_view) == null
        typeOrientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        containerId = if (typeOrientation && typeLayout) R.id.fragment_container_view_right
                      else R.id.fragment_container_view
        containerIdLists = if (typeOrientation && typeLayout) R.id.fragment_container_view_left
                       else R.id.fragment_container_view
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
            .addToBackStack(null)
            .commit()
        listFragmentsViewModel.addFragmentTagToList(tag)
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
        if (status) networkBarDisplayStatus = true
        if (networkBarDisplayStatus) {
            if (status) { // Hide bar -- Wifi/Data network active
                val fadeOutAnim: ViewPropertyAnimator = binding.barConnectivityInfo.animate()
                    .alpha(0.0f).setDuration(200)
                fadeOutAnim.withEndAction { binding.barConnectivityInfo.visibility = View.GONE}
            }
            else { // Display bar -- No network active
                binding.barConnectivityInfo.visibility = View.VISIBLE
                ViewCompat.setElevation(binding.barConnectivityInfo, 10F)
                val fadeInAnim: ViewPropertyAnimator = binding.barConnectivityInfo.animate()
                    .alpha(1.0f).setDuration(200)
                fadeInAnim.start()
            }
        }
    }

    /**
     * Handles connectivity bar button.
     */
    private fun handleConnectivityBarBtnListener() {
        binding.barConnectivityInfoBtnClose.setOnClickListener {
            updateConnectivityBarNetworkDisplay(true)
            networkBarDisplayStatus = false
        }
    }

    /**
     * Handles visibility of background MaterialText and ImageView for "large-land"
     * activity_main.xml only
     * @param visibility : Visibility status value
     */
    fun handleBackgroundGridVisibility(visibility: Int) {
        binding.apply {
            imgBackground?.visibility = visibility
            txtBackground?.visibility = visibility }
    }

    /**
     * Handle camera photo creation.
     */
    private fun handleCameraResult() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)
        val uriString: String? = sharedPreferences.getString(AppInfo.PREF_CURRENT_URI, "")
        if (uriString != null) {
            val fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_NEW_ESTATE)
                    as FragmentNewEstate
            fragment.addNewPhotoUri(uriString.toUri()) }
    }

    /**
     * Handle data intent result after an image selection in gallery.
     * @param data : data intent
     */
    private fun handleGalleryResult(data: Intent) {
        val imageMediaUri: Uri? = data.data
        if (isFragmentDisplayed(AppInfo.TAG_FRAGMENT_NEW_ESTATE)) {
            val fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_NEW_ESTATE)
                    as FragmentNewEstate
            if (imageMediaUri != null) fragment.addNewPhotoUri(imageMediaUri) }
    }

    /**
     * Handle data intent result from autocomplete request.
     * @param resultCode : result code
     * @param data : data intent
     */
    private fun handleAutocompleteResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    val fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_NEW_ESTATE)
                            as FragmentNewEstate
                    estatesViewModel.updateLocationSelectedEstate(place, this)
                    fragment.updateLocationDisplayed()} }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if permissions are all granted
        if (permissions[0] == android.Manifest.permission.READ_EXTERNAL_STORAGE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && permissions[1] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
            && permissions[2] == android.Manifest.permission.CAMERA
            && grantResults[2] == PackageManager.PERMISSION_GRANTED)
            handleMediaPermissionsRequestResult()
        if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && isFragmentDisplayed(AppInfo.TAG_FRAGMENT_MAP))
            handleLocationPermissionRequestResult()
    }

    /**
     * Handles storage of [SharedPreferences] value associated to the number of permissions
     * requests for media access.
     */
    private fun handleMediaPermissionsRequestResult() {
        val nbRequestsSaved = getSharedPreferences(AppInfo.FILE_SHARED_PREF,
            Context.MODE_PRIVATE)
        // Reset number of permission requests
        nbRequestsSaved.edit { putInt(AppInfo.PREF_PERMISSIONS, 0).apply() }
    }

    /**
     * Handles storage of [SharedPreferences] value associated to the number of permission
     * requests for location access, and updates displayed [FragmentMap] instance.
     */
    private fun handleLocationPermissionRequestResult() {
        val fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_MAP)
                as FragmentMap
        fragment.apply {
            if (GPSAccessHandler.checkLocationPermission(activity as MainActivity)) {
                initializeMapOptions()
                initializeCameraPositionOnMap()
            } }
        val nbRequestSaved = getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)
        // Reset number of permission requests
        nbRequestSaved.edit { putInt(AppInfo.PREF_PERMISSION_LOCATION, 0).apply() }
    }

    /**
     * Displays an [AlertDialog] for new agent creation.
     */
    fun showDialogAddAgent(){
        builderAddAgentDialog.apply {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).isEnabled= false }
    }

    /**
     * Displays an [AlertDialog] for logout.
     */
    fun showDialogLogout() = builderLogoutDialog.show()

    /**
     * Restores displayed dialogs after a configuration change.
     * @param savedInstanceState : Bundle
     */
    private fun restoreDialogs(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            if (it.getBoolean(AppInfo.DIALOG_ADD_AGENT_KEY)) {
                showDialogAddAgent()
                restoreAddAgentDialogText(savedInstanceState) }
            if (it.getBoolean(AppInfo.DIALOG_LOGOUT_KEY)) showDialogLogout()
        }
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderAddAgentDialog] property.
     */
    private fun initializeDialogAddAgent() {
        val viewAddNewAgent: View? = getViewFromLayoutInflater(R.layout.dialog_agent_creation)
        val firstNameAgentInputEdit = viewAddNewAgent
            ?.findViewById<TextInputEditText>(R.id.first_name_text_input_edit)
        val lastNameAgentInputEdit = viewAddNewAgent
            ?.findViewById<TextInputEditText>(R.id.last_name_text_input_edit)
        handleTextWatchersNameAgent(firstNameAgentInputEdit, lastNameAgentInputEdit)
        builderAddAgentDialog = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.str_dialog_add_agent_title))
            .setView(viewAddNewAgent)
            .setPositiveButton(resources.getString(R.string.str_dialog_confirm)) { _, _ ->
                addNewAgent(firstNameAgentInputEdit, lastNameAgentInputEdit)
                firstNameAgentInputEdit?.text?.clear()
                lastNameAgentInputEdit?.text?.clear() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) { _, _ -> }
            .create()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderLogoutDialog] property.
     */
    private fun initializeDialogLogout() {
        builderLogoutDialog = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.str_dialog_logout_title))
            .setMessage(resources.getString(R.string.str_dialog_logout_message))
            .setPositiveButton(resources.getString(R.string.str_dialog_confirm))  { _, _ ->
                AuthenticationFirebase.logoutUser(this) { onLogout(it)} }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) { _, _ -> }
            .create()
    }

    /**
     * Displays a [Snackbar] for log out information.
     * @param status : status of the logout operation
     */
    private fun onLogout(status: Boolean) {
        if (status) {
            Snackbar.make(binding.mainActivity,
                resources.getString(R.string.str_dialog_logout_title), Snackbar.LENGTH_SHORT).show()
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        else
            Snackbar.make(binding.mainActivity, resources.getString(R.string.str_error_logout),
                Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Gets layout inflater using context
     * @param layout : layout to inflate
     * @return : inflated view
     */
    private fun getViewFromLayoutInflater(@LayoutRes layout: Int): View? {
        val inflater: LayoutInflater? = getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as? LayoutInflater
        return inflater?.inflate(layout, null)
    }

    /**
     * Handles "text changed" catch event for both [firstNameAgentInputEdit] and [lastNameAgentInputEdit]
     * [TextInputEditText].
     * @param firstNameAgentInputEdit : [TextInputEditText] containing the first name agent string
     * @param lastNameAgentInputEdit : [TextInputEditText] containing the last name agent string
     */
    private fun handleTextWatchersNameAgent(firstNameAgentInputEdit: TextInputEditText?,
                                            lastNameAgentInputEdit: TextInputEditText?) {
        lastNameAgentInputEdit?.addTextChangedListener(object: CustomTextWatcher() {
            override fun afterTextChanged(sequence: Editable?) {
                firstNameAgentInputEdit?.let { itFirstName ->
                    sequence?.let { itSequence ->
                        lastNameAgent = sequence.toString()
                        builderAddAgentDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                            itFirstName.length() > 0 && itSequence.isNotEmpty() } } }
        })
        firstNameAgentInputEdit?.addTextChangedListener(object: CustomTextWatcher() {
            override fun afterTextChanged(sequence: Editable?) {
                lastNameAgentInputEdit?.let { itLastName ->
                    sequence?.let { itSequence ->
                        firstNameAgent = itSequence.toString()
                        builderAddAgentDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                            itLastName.length() > 0 && itSequence.isNotEmpty() } } }
        })
    }

    /**
     * Sends new created agent to [ListEstatesViewModel] for database insertion.
     * @param firstNameAgentInputEdit : [TextInputEditText] containing the first name agent string
     * @param lastNameAgentInputEdit : [TextInputEditText] containing the last name agent string
     */
    private fun addNewAgent(firstNameAgentInputEdit: TextInputEditText?,
                            lastNameAgentInputEdit: TextInputEditText?) {
        if (firstNameAgentInputEdit != null && lastNameAgentInputEdit != null) {
            val agent = Agent(id = 0, firstName = firstNameAgent, lastName = lastNameAgent)
            listEstatesViewModel.insertAgentInDatabase(agent) }
    }

    /**
     * Restores first and last name displayed in [builderAddAgentDialog], after a configuration
     * change.
     * @param savedInstanceState : Bundle
     */
    private fun restoreAddAgentDialogText(savedInstanceState: Bundle?) {
        builderAddAgentDialog.apply {
            val firstNameAgentInputEdit = this
                .findViewById<TextInputEditText>(R.id.first_name_text_input_edit)
            val lastNameAgentInputEdit = this
                .findViewById<TextInputEditText>(R.id.last_name_text_input_edit)
            savedInstanceState?.getString(AppInfo.FIRST_NAME_AGENT_KEY)?.let {
                firstNameAgentInputEdit?.text = StringHandler.convertStringToEditable(it) }
            savedInstanceState?.getString(AppInfo.LAST_NAME_AGENT_KEY)?.let {
                lastNameAgentInputEdit?.text = StringHandler.convertStringToEditable(it) }
        }
    }

    /**
     * Handle click events on list item.
     * @param position : position in list
     */
    fun handleClickOnEstateView(position: Int) {
        /*if (typeOrientation) {
            val fragmentListEstate = supportFragmentManager
                .findFragmentByTag(AppInfo.TAG_FRAGMENT_LIST_ESTATE) as FragmentListEstate
            fragmentListEstate.let {
                it.handleClickOnEstateItem(position)
            }
        }
        else {
            listEstatesViewModel.setSelectedEstate(position)
            displayFragmentDetails()
        }*/
    }

    /**
     * Check if main activity child fragment associated with specified [tag] is displayed.
     * @param tag : Tag fragment
     */
    private fun isFragmentDisplayed(tag: String): Boolean =
        supportFragmentManager.findFragmentByTag(tag) != null

    /**
     * Clear back stack after a configuration change.
     */
    private fun deleteOldFragments() {
        // Handle FragmentListEstates
        val fragment: Fragment?
        if (containerIdLists == R.id.fragment_container_view_left) { // layout activity_main (large)
            fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_LIST_ESTATE)
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commitNow()
        }
        else { // layout activity_main
            fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_LIST_ESTATE_LARGE)
            if (fragment != null)
                supportFragmentManager.beginTransaction().remove(fragment).commitNow()
        }
        // Handle others fragments
        for (i in 0 until supportFragmentManager.backStackEntryCount)
            supportFragmentManager.popBackStackImmediate()
    }

    /**
     * Recreates fragments after a configuration change.
     */
    private fun restoreFragmentsFromBackStack() {
        val list = mutableListOf<String>()
        list.addAll(listFragmentsViewModel.listTags)
        listFragmentsViewModel.listTags.clear()
        for (i in 0 until list.size) {
            when(list[i]) {
                AppInfo.TAG_FRAGMENT_ESTATE_DETAILS -> {
                    launchTransaction(containerId, FragmentEstateDetails.newInstance(),
                                                                AppInfo.TAG_FRAGMENT_ESTATE_DETAILS) }
                AppInfo.TAG_FRAGMENT_NEW_ESTATE -> {
                    launchTransaction(containerId, FragmentNewEstate.newInstance(),
                                                                  AppInfo.TAG_FRAGMENT_NEW_ESTATE) }
                AppInfo.TAG_FRAGMENT_SETTINGS -> {
                    launchTransaction(containerId, FragmentSettings.newInstance(),
                                                                    AppInfo.TAG_FRAGMENT_SETTINGS) }
                AppInfo.TAG_FRAGMENT_SEARCH -> {
                    launchTransaction(containerIdLists, FragmentSearch.newInstance(),
                                                                    AppInfo.TAG_FRAGMENT_SEARCH) }
                AppInfo.TAG_FRAGMENT_MAP -> {
                    launchTransaction(containerId, FragmentMap.newInstance(),
                                                                    AppInfo.TAG_FRAGMENT_MAP) }
            }
        }
    }

    /**
     * Remove fragment on top of back stack after a "back button pressed" event.
     * @return : returns true if a fragment has been removed
     */
    private fun removeFragmentFromBackStack(): Boolean {
        var removed = false
        if (listFragmentsViewModel.listTags.isNotEmpty()) {
            when (listFragmentsViewModel.listTags.last()){
                AppInfo.TAG_FRAGMENT_ESTATE_DETAILS -> {
                    removeFragment(AppInfo.TAG_FRAGMENT_ESTATE_DETAILS)
                    removed = true
                }
                AppInfo.TAG_FRAGMENT_NEW_ESTATE -> {
                    val fragment = supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_NEW_ESTATE)
                    if (!(fragment as FragmentNewEstate).confirmExit)
                        fragment.builderCancelEstateDialog?.show()
                    else removeFragment(AppInfo.TAG_FRAGMENT_NEW_ESTATE)
                    removed = true
                }
                AppInfo.TAG_FRAGMENT_SETTINGS -> {
                    removeFragment(AppInfo.TAG_FRAGMENT_SETTINGS)
                    removed = true
                }
                AppInfo.TAG_FRAGMENT_SEARCH -> {
                    removeFragment(AppInfo.TAG_FRAGMENT_SEARCH)
                    removed = true
                }
                AppInfo.TAG_FRAGMENT_MAP -> {
                    removeFragment(AppInfo.TAG_FRAGMENT_MAP)
                    removed = true
                }
            }
        }
        return removed
    }

    /**
     * Displays [FragmentEstateDetails].
     */
    fun displayFragmentDetails() = launchTransaction(containerId, FragmentEstateDetails.newInstance(),
                                                                AppInfo.TAG_FRAGMENT_ESTATE_DETAILS)

    /**
     * Displays [FragmentSettings].
     */
    fun displayFragmentSettings() = launchTransaction(containerId, FragmentSettings.newInstance(),
                                                                      AppInfo.TAG_FRAGMENT_SETTINGS)


    /**
     * Displays [FragmentSearch].
     */
    fun displayFragmentSearch() = launchTransaction(containerIdLists, FragmentSearch.newInstance(),
                                                                        AppInfo.TAG_FRAGMENT_SEARCH)


    /**
     * Displays [FragmentMap].
     */
    fun displayFragmentMap() = launchTransaction(containerId, FragmentMap.newInstance(),
                                                                           AppInfo.TAG_FRAGMENT_MAP)

    /**
     * Displays [FragmentNewEstate].
     */
    fun displayFragmentNewEstate() {
        val fragment: FragmentNewEstate = FragmentNewEstate.newInstance()
        launchTransaction(containerId, fragment, AppInfo.TAG_FRAGMENT_NEW_ESTATE)
    }

    /**
     * Removes a fragment.
     * @param tag :tag associated to the fragment to remove.
     */
    fun removeFragment(tag: String) {
        // Remove fragment
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        fragment?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
            supportFragmentManager.popBackStackImmediate() }
        // Handle views update for landscape orientation (large screen)
        updateActivityViewsIfBackStackIsEmpty()
        // Update viewModel
        listFragmentsViewModel.removeTagToList(tag)
        // Update views
        if (tag == AppInfo.TAG_FRAGMENT_ESTATE_DETAILS) clearSelectionInFragmentListEstate()
    }

    /**
     * Updates parent activity views according to the back stack status.
     */
    private fun updateActivityViewsIfBackStackIsEmpty() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            if (typeOrientation && typeLayout) handleBackgroundGridVisibility(View.VISIBLE)
            setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)
            getFragmentList()?.let {
                (it as FragmentListEstate).handleFabVisibility(View.VISIBLE)
            }
        }
    }

    /**
     * Clear status of the selected item in list.
     */
    private fun clearSelectionInFragmentListEstate() {
        val fragmentListEstate: Fragment? = getFragmentList()
        if (fragmentListEstate != null) {
            (fragmentListEstate as FragmentListEstate).apply {
                clearCurrentSelection()
            }
        }
    }
    /**
     * Gets which [FragmentListEstate] is displayed according to the type of screen and orientation.
     * @return : fragment displayed
     */
    private fun getFragmentList(): Fragment? =
        if (typeOrientation && typeLayout)
            supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_LIST_ESTATE_LARGE)
        else
            supportFragmentManager.findFragmentByTag(AppInfo.TAG_FRAGMENT_LIST_ESTATE)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // From camera : Get Uri from saved value in SharedPreferences
        if (resultCode == RESULT_OK && requestCode
            == AppInfo.REQUEST_IMAGE_CAPTURE) handleCameraResult()
        // From Gallery : get Uri from data Intent
        if (resultCode == RESULT_OK && data != null) handleGalleryResult(data)
        // Autocomplete request result
        if (requestCode == 200) handleAutocompleteResult(resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(AppInfo.DIALOG_ADD_AGENT_KEY, builderAddAgentDialog.isShowing)
            putBoolean(AppInfo.DIALOG_LOGOUT_KEY, builderLogoutDialog.isShowing)
            putString(AppInfo.FIRST_NAME_AGENT_KEY, firstNameAgent)
            putString(AppInfo.LAST_NAME_AGENT_KEY, lastNameAgent)
            putBoolean(AppInfo.NETWORK_BAR_STATUS_KEY, networkBarDisplayStatus)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (builderAddAgentDialog.isShowing) builderAddAgentDialog.dismiss()
        if (builderLogoutDialog.isShowing) builderLogoutDialog.dismiss()
    }

    override fun onBackPressed() {
        if (!removeFragmentFromBackStack()) finishAffinity()
    }
}