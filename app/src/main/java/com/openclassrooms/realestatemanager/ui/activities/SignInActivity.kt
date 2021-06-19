package com.openclassrooms.realestatemanager.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 100
    }
    private lateinit var binding: ActivitySignInBinding

    private val providers: List<AuthUI.IdpConfig> = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.TwitterBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is currently logged-in
        if (FirebaseAuth.getInstance().currentUser != null) {
            // Launch MainActivity
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        handleConnexionButtonsListeners()
    }

    private fun handleConnexionButtonsListeners() {
        val layout: AuthMethodPickerLayout = AuthMethodPickerLayout
            .Builder(R.layout.custom_auth_layout)
            .setGoogleButtonId(R.id.google_auth_btn)
            .setFacebookButtonId(R.id.facebook_auth_btn)
            .setTwitterButtonId(R.id.twitter_auth_btn)
            .build()

        binding.signInButton.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setAuthMethodPickerLayout(layout)
                    .setTheme(R.style.SignInTheme)
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN
            )
        }
    }
}