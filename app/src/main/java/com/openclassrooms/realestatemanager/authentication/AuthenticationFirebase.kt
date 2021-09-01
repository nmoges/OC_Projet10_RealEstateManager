package com.openclassrooms.realestatemanager.authentication

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.openclassrooms.realestatemanager.ui.activities.MainActivity

/**
 * Provides Firebase back-end service
 */
object AuthenticationFirebase {

    /**
     * Handles logout user process.
     * @param context: Context
     * @param onLogout : Lambda function catching logout operation value (boolean)
     */
    fun logoutUser(context: Context, onLogout : (Boolean) -> Unit) {
        AuthUI.getInstance().signOut(context)
            .addOnFailureListener { onLogout(false) }
            .addOnSuccessListener { onLogout(true) }
    }
}