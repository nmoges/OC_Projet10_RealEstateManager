package com.openclassrooms.realestatemanager.authentication

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.openclassrooms.realestatemanager.ui.activities.MainActivity

object AuthenticationFirebase {
    fun logoutUser(context: Context, onLogout : (Boolean) -> Unit) {
        AuthUI.getInstance().signOut(context)
            .addOnFailureListener { onLogout(false) }
            .addOnSuccessListener { onLogout(true) }
    }
}