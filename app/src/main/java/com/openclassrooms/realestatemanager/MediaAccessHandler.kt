package com.openclassrooms.realestatemanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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

        /** Contains current path for a new photo. */
        lateinit var currentPhotoPath: String

        /**
         * Handles [File] creation when camera is launched to take a new photo.
         * @param activity : Main activity
         */
        private fun createImageFile(activity: MainActivity): File? {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile("JPEG_${timeStamp}", ".jpg", storageDir)
                       .apply { currentPhotoPath = absolutePath }
        }

        /**
         * Opens camera device activity.
         * @param activity : Main activity
         */
        @SuppressLint("QueryPermissionsNeeded")
        fun openCamera(activity: MainActivity) {
            // Init SharedPreferences file
            val filePreferences: SharedPreferences =
                       activity.getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)
            val editor = filePreferences.edit()
            // Define intent
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { photoIntent ->
                photoIntent.resolveActivity(activity.packageManager)?.also {
                    // Create file
                    val photoFile: File? = try { createImageFile(activity) }
                                           catch (exception: IOException) { null }
                    photoFile?.also {
                        val photoUri: Uri = FileProvider.getUriForFile(activity,
                    "com.openclassrooms.realestatemanager.fileprovider", it)
                        // Save current uri in sharedPreferences
                        editor.putString(AppInfo.PREF_CURRENT_URI, photoUri.toString()).apply()
                        // Start activity with intent
                        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        activity.startActivityForResult(photoIntent, AppInfo.REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }
}