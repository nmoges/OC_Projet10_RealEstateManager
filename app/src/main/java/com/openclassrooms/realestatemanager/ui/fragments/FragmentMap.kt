package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
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
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.receiver.GPSBroadcastReceiver
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.GPSAccessHandler
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

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
    private lateinit var map: GoogleMap

    /** Contains current user position */
    private var currentPosition: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())
                                                .get(ListEstatesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setToolbarProperties(
            R.string.str_toolbar_fragment_list_estate_title, true)
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        initializeMap()
        initializeGPSBroadcast()
        handleFloatingButtonClickEvents()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (activity as MainActivity).onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).registerReceiver(gpsBroadcastReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        updateFloatingButtonIconDisplay(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).unregisterReceiver(gpsBroadcastReceiver)
    }

    /**
     * Initializes a [SupportMapFragment] containing a [GoogleMap].
     */
    private fun initializeMap() {
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Displays estates in a [GoogleMap]. Only estates close to user position are
     * displayed (distance < 1000m).
     * @param map : Google map
     */
    // TODO() : Add observer on listEstatesViewModel
    private fun displayEstatesMarkersOnMap(map: GoogleMap) {
        currentPosition?.let { itPosition ->
            listEstatesViewModel.listEstates.value?.forEach { itEstate ->
                val results = FloatArray(1)
                Location.distanceBetween(
                    itPosition.latitude,
                    itPosition.longitude,
                    itEstate.location.latitude,
                    itEstate.location.longitude,
                    results
                )
                // Display estates which distance is < 1000m from user location
                if (results[0] < 1000) map.addMarker(MarkerOptions()
                    .position(LatLng(itEstate.location.latitude, itEstate.location.longitude))
                    .title(itEstate.type))
            }
        }
    }

    /**
     * Initializes [CameraUpdate] associated to the displayed [GoogleMap].
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    fun initializeCameraPositionOnMap() {
        (activity as MainActivity).locationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener {
                currentPosition = LatLng(it.latitude, it.longitude)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPosition, 18.0f)
                map.moveCamera(cameraUpdate)
                displayEstatesMarkersOnMap(map)
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
        map.apply {
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
        if (gpsStatus) {
            binding.fabGps.setImageDrawable(ResourcesCompat
                   .getDrawable(resources, R.drawable.ic_baseline_gps_fixed_24dp_white, null))
        }
        else {
            binding.fabGps.setImageDrawable(ResourcesCompat
                   .getDrawable(resources, R.drawable.ic_baseline_gps_off_24dp_white, null))
        }
    }

    /**
     * Handles user clicks on floating action button.
     */
    private fun handleFloatingButtonClickEvents() {
        binding.fabGps.setOnClickListener {
            // If Location access not enabled
            if (!GPSAccessHandler.checkLocationPermission(activity as MainActivity)) {
                GPSAccessHandler.requestPermissionLocation(activity as MainActivity)
            }
            else { // Else enabled
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
            map.animateCamera(cameraUpdate)
        }
    }

    /**
     * Initializes a [GPSBroadcastReceiver] for GPS activation/deactivation events.
     */
    private fun initializeGPSBroadcast() {
        gpsBroadcastReceiver = GPSBroadcastReceiver {
            onGpsEventDetected(it)
        }
    }

    /**
     * Catches a GPS activation/deactivation event.
     * @param status : GPS status.
     */
    private fun onGpsEventDetected(status: Boolean) {
        updateFloatingButtonIconDisplay(status)
    }

    /**
     * Displayes an [AlertDialog] for GPS activation.
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
}