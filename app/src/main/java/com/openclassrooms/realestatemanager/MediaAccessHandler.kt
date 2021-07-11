package com.openclassrooms.realestatemanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.ui.activities.MainActivity

/**
 * Class defining a set of functions to access media storage.
 */
class MediaAccessHandler {

    companion object {
        /** [SharedPreferences] file */
        private lateinit var filePreferences: SharedPreferences

        /** [SharedPreferences] file editor */
        private lateinit var editor: SharedPreferences.Editor

        /** Contains the number of permission requests sent by user */
        private var nbRequest: Int = 0

        /** List of necessary permissions */
        private var permissions:  Array<String> = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        /**
         * Initializes [nbRequest] using [SharedPreferences].
         * @param activity : Main activity
         */
        @SuppressLint("CommitPrefEdits")
        fun initializeNbPermissionRequests(activity: MainActivity) {
            filePreferences = activity.getSharedPreferences(AppInfo.FILE_SHARED_PREF,
                                                            Context.MODE_PRIVATE)

            // Get number of permissions requests already sent
            nbRequest = filePreferences.getInt(AppInfo.PREF_PERMISSIONS, 0)

            editor = filePreferences.edit()
        }

        /**
         * Checks if requested permission is granted.
         * @param activity : Main activity
         */
        fun checkPermissions(activity: MainActivity):Boolean =
                ContextCompat.checkSelfPermission(activity, permissions[0]) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, permissions[1]) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, permissions[2]) == PERMISSION_GRANTED

        /**
         * Requests permissions to the OS.
         * @param activity : Main activity
         */
        fun requestPermission(activity: MainActivity) {

            if (nbRequest <= 1) {// First request
                ActivityCompat.requestPermissions(activity, permissions, AppInfo.REQUEST_PERMISSIONS_CODE)
            }
            else { // "Don't ask again" checked
                if (!shouldShowRequestPermissionRationale(activity, permissions[0])
                    && !shouldShowRequestPermissionRationale(activity, permissions[1])
                    && !shouldShowRequestPermissionRationale(activity, permissions[2]))
                    displayAccessSettingsDialog(activity)
                else // "Don't ask again" not checked
                    ActivityCompat.requestPermissions(activity, permissions, AppInfo.REQUEST_PERMISSIONS_CODE)
            }
            nbRequest++
            editor.putInt(AppInfo.PREF_PERMISSIONS, nbRequest).apply()
        }

        /**
         * Opens device camera.
         * @param activity : Main activity
         */

        fun openCamera(activity: MainActivity) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                activity.startActivityForResult(cameraIntent, AppInfo.REQUEST_IMAGE_CAPTURE)
            } catch (exception: ActivityNotFoundException) {
                exception.printStackTrace()
            }
        }

        /**
         * Open device photos gallery.
         * @param activity : Main activity
         */
        fun openPhotosGallery(activity: MainActivity) {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            activity.startActivityForResult(intent, AppInfo.REQUEST_IMAGE_GALLERY)
        }

        /**
         * Displays an educational UI for an associated permission.
         * @param activity : Main activity
         */
        private fun displayAccessSettingsDialog(activity: MainActivity) {

            val builderAccessSettingsDialog = AlertDialog.Builder(activity)
                .setTitle(activity.resources.getString(R.string.str_dialog_permission_access_title))
                .setMessage(activity.resources.getString(R.string.str_dialog_permission_access_message))
                .setPositiveButton(activity.resources
                        .getString(R.string.str_dialog_permission_access_button_settings)) { _, _ ->
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse(activity.resources.getString(R.string.uri_string_package))
                    activity.startActivity(intent)
                }
                .setNegativeButton(activity.resources.getString(R.string.str_dialog_button_cancel)) { _,_ -> }
                .create()

            builderAccessSettingsDialog.show()
        }
    }
}