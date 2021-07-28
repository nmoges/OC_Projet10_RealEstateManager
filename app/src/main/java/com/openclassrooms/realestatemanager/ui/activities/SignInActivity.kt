package com.openclassrooms.realestatemanager.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivitySignInBinding

/**
 * [AppCompatActivity] subclass which defines the sign-in activity of the application.
 */
class SignInActivity : AppCompatActivity() {

    companion object {
        /** Request code */
        const val RC_SIGN_IN = 100
    }

    /** Binding parameter */
    private lateinit var binding: ActivitySignInBinding

    /** Contains list of providers used to sign-in */
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
        if (FirebaseAuth.getInstance().currentUser != null) startMainActivity()

        handleConnexionButtonsListeners()
    }

    /**
     * Handles click events of buttons.
     */
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

    /**
     * Intent launcher for auto-generated authentication activity
     */
    private fun startMainActivity() {
        // Launch MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Handles results from Firebase auto-generated activity.
     * @param requestCode : request code
     * @param resultCode : result code
     * @param data : returned intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response: IdpResponse? = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) startMainActivity()
            else { // handle response
                when {
                    response == null -> {
                        Snackbar.make(binding.signInActivityLayout,
                                      R.string.snack_bar_auth_cancelled,
                                      Snackbar.LENGTH_SHORT).show()
                    }
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> {
                        Snackbar.make(binding.signInActivityLayout,
                                      R.string.snack_bar_error_no_network,
                                      Snackbar.LENGTH_SHORT).show()
                    }
                    response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> {
                        Snackbar.make(binding.signInActivityLayout,
                                      R.string.snack_bar_error_unknown,
                                      Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}