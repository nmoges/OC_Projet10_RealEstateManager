package com.openclassrooms.realestatemanager

/**
 * Contains RealEstateManager application constant values.
 */
object AppInfo {

    // SharedPreferences parameters
    const val FILE_SHARED_PREF = "FILE_SHARED_PREF"
    const val PREF_PERMISSIONS = "PREF_PERMISSIONS"
    const val PREF_PERMISSION_LOCATION = "PREF_PERMISSION_LOCATION"
    const val PREF_CURRENCY = "PREF_CURRENCY"
    const val PREF_CURRENT_URI = "PREF_CURRENT_URI"
    const val PREF_USER_LOC_LATITUDE = "PREF_USER_LOC_LATITUDE"
    const val PREF_USER_LOC_LONGITUDE = "PREF_USER_LOC_LONGITUDE"

    // Request codes
    const val REQUEST_PERMISSIONS_CODE = 101
    const val REQUEST_IMAGE_GALLERY = 1
    const val REQUEST_IMAGE_CAPTURE = 2

    // Bundle keys
    const val DIALOG_ADD_AGENT_KEY = "DIALOG_ADD_AGENT_KEY"
    const val FIRST_NAME_AGENT_KEY = "FIRST_NAME_AGENT_KEY"
    const val LAST_NAME_AGENT_KEY = "LAST_NAME_AGENT_KEY"
    const val DIALOG_LOGOUT_KEY = "DIALOG_LOGOUT_KEY"
    const val DIALOG_CURRENCY_SELECT_KEY = "DIALOG_CURRENCY_SELECT_KEY"
    const val DIALOG_DELETE_ACCOUNT_KEY = "DIALOG_DELETE_ACCOUNT_KEY"

    // Bundle keys - Search Fragment
    const val NETWORK_BAR_STATUS_KEY = "NETWORK_BAR_STATUS_KEY"

    // Tags
    const val TAG_FRAGMENT_NEW_ESTATE = "TAG_FRAGMENT_NEW_ESTATE"
    const val TAG_FRAGMENT_SETTINGS = "TAG_FRAGMENT_SETTINGS"
    const val TAG_FRAGMENT_LIST_ESTATE = "TAG_FRAGMENT_LIST_ESTATE"
    const val TAG_FRAGMENT_LIST_ESTATE_LARGE = "TAG_FRAGMENT_LIST_ESTATE_LARGE"
    const val TAG_FRAGMENT_ESTATE_DETAILS = "TAG_FRAGMENT_ESTATE_DETAILS"
    const val TAG_FRAGMENT_MAP = "TAG_FRAGMENT_MAP"
    const val TAG_FRAGMENT_SEARCH = "TAG_FRAGMENT_SEARCH"

    // Notification parameters
    const val CHANNEL_ID = "CHANNEL_ID"
    const val CHANNEL_NAME = "channel_real_estate_manager_app"
    const val CHANNEL_DESCRIPTION = "description"

    // LocationListener parameters
    const val LOCATION_REFRESH_TIME: Long = 15000
    const val LOCATION_REFRESH_DISTANCE: Float = 10f

    // Map parameters
    const val DEFAULT_CAMERA_ZOOM = 18.0f
}