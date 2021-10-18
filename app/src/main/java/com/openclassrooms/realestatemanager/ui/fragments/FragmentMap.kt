package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.data.model.Estate
import com.openclassrooms.realestatemanager.receiver.GPSBroadcastReceiver
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.GPSAccessHandler
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import java.lang.IllegalStateException

/**
 * [Fragment] subclass used to display a google map.
 */
class FragmentMap : Fragment(), OnMapReadyCallback {

    companion object {
        @JvmStatic
        fun newInstance() = FragmentMap()
    }

    /** View Binding parameter */
    private lateinit var binding: FragmentMapBinding

    /** Contains a reference to a [ListEstatesViewModel] */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains a reference to a [LocationManager] */
    private lateinit var locationManager: LocationManager

    /** Contains a reference to a [GPSBroadcastReceiver] */
    private lateinit var gpsBroadcastReceiver: GPSBroadcastReceiver

    /** Contains reference to a google map */
    private var map: GoogleMap? = null

    /** Contains current user position */
    private var currentPosition: LatLng? = null

    /** Location listener for user location updates */
    private lateinit var locationListener: LocationListener

    /** Shared preferences for saving user location */
    private var sharedPrefLocation: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setToolbarProperties(
            R.string.str_toolbar_fragment_list_estate_title, true)
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        initializeSharedPrefLocation()
        initializeMap()
        initializeGPSBroadcast()
        handleFloatingButtonClickEvents()
        locationListener = LocationListener {
            map?.let { itMap ->
                itMap.clear()
                displayEstatesMarkersOnMap()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (activity as MainActivity).onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).registerReceiver(gpsBroadcastReceiver,
                                             IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        updateFloatingButtonIconDisplay(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        handleLocationListener(true)
    }

    override fun onPause() {
        (activity as MainActivity).unregisterReceiver(gpsBroadcastReceiver)
        handleLocationListener(false)
        saveCurrentLocation()
        super.onPause()
    }

    /**
     * Handles location updates listener.
     * @param status : listener enabled status
     */
    private fun handleLocationListener(status: Boolean) {
        if (GPSAccessHandler.checkLocationPermission(activity as MainActivity)
            && GPSAccessHandler.isGPSEnabled(locationManager)) {
                if (status)  locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    AppInfo.LOCATION_REFRESH_TIME,
                    AppInfo.LOCATION_REFRESH_DISTANCE,
                    locationListener)
                else locationManager.removeUpdates(locationListener)
        }
    }

    /**
     * Initializes a [SupportMapFragment] containing a [GoogleMap].
     */
    private fun initializeMap() {
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.google_map)
                                              as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Displays estates in a [GoogleMap]. Only estates close to user position are
     * displayed (distance < 1000m).
     */
    private fun displayEstatesMarkersOnMap() {
        currentPosition?.let { itPosition ->
            try {
                listEstatesViewModel.listEstates.observe(viewLifecycleOwner, {
                    it.forEach { itEstate ->
                        if (GPSAccessHandler.checkDistanceEstateFromGPSLocation(
                                LatLng(itPosition.latitude, itPosition.longitude),
                                itPosition)) {
                            map?.addMarker(MarkerOptions()
                                .position(LatLng(itEstate.location.latitude, itEstate.location.longitude))
                                .title(itEstate.type))
                        }
                    }
                    handleClicksOnMarkers(it)
                })
            } catch (exception: IllegalStateException) {
                exception.printStackTrace()
            }
        }
    }

    /**
     * Initializes [CameraUpdate] associated to the displayed [GoogleMap].
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    fun initializeCameraPositionOnMap() {
        if (GPSAccessHandler.isGPSEnabled(locationManager)) {
            (activity as MainActivity).locationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { itLoc ->
                    currentPosition = LatLng(itLoc.latitude, itLoc.longitude)
                    currentPosition?.let { itLatLng ->
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(itLatLng,
                                                                        AppInfo.DEFAULT_CAMERA_ZOOM)
                        map?.moveCamera(cameraUpdate)
                        displayEstatesMarkersOnMap()
                    }
                }
        }
    }

    /**
     * Called when a [GoogleMap] is displayed and ready to be used.
     * @param googleMap : [GoogleMap] displayed
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            map = googleMap
            if (GPSAccessHandler.checkLocationPermission(activity as MainActivity)) {
                initializeMapOptions()
                displayOldUserLocation()
                initializeCameraPositionOnMap()
            }
            else GPSAccessHandler.requestPermissionLocation(activity as MainActivity)
        }
    }

    /**
     * Initializes [GoogleMap] options.
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    fun initializeMapOptions() {
        map?.apply {
            isMyLocationEnabled = true
            uiSettings.apply {
                isMyLocationButtonEnabled = false
                isCompassEnabled = true
                isMapToolbarEnabled = false
            }
        }
    }

    /**
     * Updates the floating action button icon displayed.
     * @param gpsStatus : Activation status of the device GPS.
     */
    private fun updateFloatingButtonIconDisplay(gpsStatus: Boolean) {
        if (gpsStatus)
            binding.fabGps.setImageDrawable(ResourcesCompat
                   .getDrawable(resources, R.drawable.ic_baseline_gps_fixed_24dp_white, null))
        else
            binding.fabGps.setImageDrawable(ResourcesCompat
                   .getDrawable(resources, R.drawable.ic_baseline_gps_off_24dp_white, null))
    }

    /**
     * Handles user clicks on floating action button.
     */
    private fun handleFloatingButtonClickEvents() {
        binding.fabGps.setOnClickListener {
            // If Location access not enabled
            if (!GPSAccessHandler.checkLocationPermission(activity as MainActivity))
                GPSAccessHandler.requestPermissionLocation(activity as MainActivity)
            else { // Else enabled
                if (GPSAccessHandler.isGPSEnabled(locationManager)) {
                    // GPS activated
                    centerCursorInCurrentUserLocation()
                }
                else {
                    // GPS deactivated
                    displayGPSEnableDialog()
                }
            }
        }
    }

    /**
     * Centers the map camera to the user location, at a predefined zoom value.
     */
    private fun centerCursorInCurrentUserLocation() {
        currentPosition?.let {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 18.0f)
            map?.animateCamera(cameraUpdate)
        }
    }

    /**
     * Initializes a [GPSBroadcastReceiver] for GPS activation/deactivation events.
     */
    @SuppressLint("MissingPermission")
    private fun initializeGPSBroadcast() {
        gpsBroadcastReceiver = GPSBroadcastReceiver { onGpsEventDetected(it) }
    }

    /**
     * Catches a GPS activation/deactivation event.
     * @param status : GPS status.
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    private fun onGpsEventDetected(status: Boolean) {
        updateFloatingButtonIconDisplay(status)
        initializeCameraPositionOnMap()
    }

    /**
     * Displays an [AlertDialog] for GPS activation.
     */
    private fun displayGPSEnableDialog() {
        val builderGPSEnableDialog = AlertDialog.Builder(activity as MainActivity)
            .setTitle((activity as MainActivity)
                .resources.getString(R.string.str_dialog_enable_gps_title))
            .setMessage((activity as MainActivity)
                .resources.getString(R.string.str_dialog_enable_gps_message))
            .setPositiveButton((activity as MainActivity).resources
                .getString(R.string.str_dialog_enable_enable_button)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) { _, _ -> }
            .create()
        builderGPSEnableDialog.show()
    }

    /**
     * Handles click events on markers.
     * @param list : List of [Estate] used to display markers in map.
     */
    @SuppressLint("PotentialBehaviorOverride")
    private fun handleClicksOnMarkers(list: List<Estate>) {
        map.let { itMap ->
            itMap?.setOnInfoWindowClickListener { itMarker ->
                var index = 0
                var found = false
                var position = 0
                while (index < list.size && !found) {
                    if (list[index].type == itMarker.title) {
                        found = true
                        position = index
                    } else index++
                }
                launchFragmentDetails(position)
            }
        }
    }

    /**
     * Displays [FragmentEstateDetails] for the selected marker.
     * @param position : position of the associated [Estate] object in the list of estates.
     */
    private fun launchFragmentDetails(position: Int) {
        listEstatesViewModel.setSelectedEstate(position)
        (activity as MainActivity).apply { handleClickOnEstateView(position) }
    }

    /**
     * Initializes a [SharedPreferences] file for user location.
     */
    private fun initializeSharedPrefLocation() {
        context?.let { sharedPrefLocation = it.getSharedPreferences(AppInfo.FILE_SHARED_PREF,
                                                                    Context.MODE_PRIVATE)
        }
    }

    /**
     * Saves current location in [SharedPreferences] file.
     */
    private fun saveCurrentLocation() {
        val latitude = currentPosition?.latitude
        val longitude = currentPosition?.longitude
        if (latitude != null && longitude != null)
            sharedPrefLocation?.let {
                with(it.edit()) {
                    putString(AppInfo.PREF_USER_LOC_LATITUDE, latitude.toString()).apply()
                    putString(AppInfo.PREF_USER_LOC_LONGITUDE, longitude.toString()).apply()
                }
            }
    }

    /**
     * Restores old user location on map.
     */
    private fun displayOldUserLocation() {
        if (GPSAccessHandler.isGPSEnabled(locationManager)) {
            val latitude = sharedPrefLocation
                ?.getString(AppInfo.PREF_USER_LOC_LATITUDE, "0.0")?.toDouble()
            val longitude = sharedPrefLocation
                ?.getString(AppInfo.PREF_USER_LOC_LONGITUDE, "0.0")?.toDouble()
            if (latitude != null && longitude != null) {
                currentPosition = LatLng(latitude, longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude),
                                                                        AppInfo.DEFAULT_CAMERA_ZOOM)
                map?.moveCamera(cameraUpdate)
            }
        }
    }
}