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

    // Request codes
    const val REQUEST_PERMISSIONS_CODE = 101
    const val REQUEST_IMAGE_GALLERY = 1
    const val REQUEST_IMAGE_CAPTURE = 2

    // Bundle keys
    const val DIALOG_RESET_KEY = "DIALOG_RESET_KEY"
    const val DIALOG_CANCEL_KEY = "DIALOG_CANCEL_KEY"
    const val DIALOG_ADD_MEDIA_KEY = "DIALOG_ADD_MEDIA_KEY"
    const val DIALOG_CONFIRM_MEDIA_KEY = "DIALOG_CONFIRM_MEDIA_KEY"
    const val DIALOG_LIST_AGENTS_KEY = "DIALOG_LIST_AGENTS_KEY"
    const val DIALOG_CONFIRM_SELL_KEY = "DIALOG_CONFIRM_SELL_KEY"
    const val TEXT_DIALOG_CONFIRM_MEDIA_KEY = "TEXT_DIALOG_CONFIRM_MEDIA_KEY"
    const val UPDATE_ESTATE_KEY = "UPDATE_ESTATE_KEY"
    const val NUMBER_PHOTO_KEY = "NUMBER_PHOTO_KEY"
    const val CONFIRM_EXIT_KEY = "CONFIRM_EXIT_KEY"
    const val ESTATE_AGENT_KEY = "ESTATE_AGENT_KEY"
    const val DIALOG_CURRENCY_SELECT_KEY = "DIALOG_CURRENCY_SELECT_KEY"
    const val DIALOG_DELETE_ACCOUNT_KEY = "DIALOG_DELETE_ACCOUNT_KEY"

    // Tags
    const val TAG_DIALOG_LIST_AGENTS = "TAG_DIALOG_LIST_AGENTS"
    const val TAG_FRAGMENT_NEW_ESTATE = "TAG_FRAGMENT_NEW_ESTATE"
    const val TAG_FRAGMENT_SETTINGS = "TAG_FRAGMENT_SETTINGS"
    const val TAG_FRAGMENT_LIST_ESTATE = "TAG_FRAGMENT_LIST_ESTATE"
    const val TAG_FRAGMENT_ESTATE_DETAILS = "TAG_FRAGMENT_ESTATE_DETAILS"
    const val TAG_FRAGMENT_MAP = "TAG_FRAGMENT_MAP"

    // Notification parameters
    const val CHANNEL_ID = "CHANNEL_ID"
    const val CHANNEL_NAME = "channel_real_estate_manager_app"
}