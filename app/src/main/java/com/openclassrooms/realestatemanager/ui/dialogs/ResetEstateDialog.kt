package com.openclassrooms.realestatemanager.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.openclassrooms.realestatemanager.R
import java.lang.IllegalStateException

/**
 * [DialogFragment] displaying a reset confirmation message for the user.
 */
class ResetEstateDialog() : DialogFragment() {

    var callback: ResetEstateDialogCallback? = null
    var text: String? = null

    constructor(callbackFragment: ResetEstateDialogCallback) : this() {
        callback = callbackFragment
    }

    companion object {
        val TAG: String = "TAG_RESET_DESCRIPTION_DIALOG"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(resources.getString(R.string.str_dialog_reset_title))
                   .setMessage(text)
                .setPositiveButton(resources.getString(R.string.str_dialog_reset_button_yes)) { _, _ -> callback?.confirmReset() }
                .setNegativeButton(resources.getString(R.string.str_dialog_reset_button_no)) { _, _ -> }
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }
}