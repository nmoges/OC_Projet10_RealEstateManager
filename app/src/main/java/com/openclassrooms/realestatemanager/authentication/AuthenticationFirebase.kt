package com.openclassrooms.realestatemanager.authentication

import android.content.Context
import com.firebase.ui.auth.AuthUI

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