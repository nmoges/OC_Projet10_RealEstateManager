package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.ViewModel

/**
 * View Model class containing the dialogs fragments status.
 */
class DialogsViewModel: ViewModel() {

    // FragmentEstateDetails.kt dialog
    var confirmDialogStatus: Boolean = false

    // FragmentNewEstate.kt dialogs
    var addPOIDialogStatus: Boolean = false

    var listAgentsDialogStatus: Boolean = false

    var cancelEstateDialogStatus: Boolean = false

    var resetEstateDialogStatus: Boolean = false

    var addMediaDialogStatus: Boolean = false

    var nameMediaDialogStatus: Boolean = false

    var textNameMediaDialog: String = ""

    var progressBarDialogStatus: Boolean = false

    // FragmentSearch.kkt dialog
    var searchDialogStatus: Boolean = false

    var confirmResetDialogStatus: Boolean = false

    var startDatePickerDialogStatus: Boolean = false

    var endDatePickerDialogStatus: Boolean = false

    // FragmentSettings.kt
    var currencySelectionDialogStatus: Boolean = false

    var deleteAccountDialogStatus: Boolean = false
}