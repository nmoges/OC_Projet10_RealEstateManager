package com.openclassrooms.data

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.google.firebase.FirebaseApp
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Subclass of AndroidJUnitRunner, defining a custom runner for Hilt dependency
 * injection in test files.
 */
class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        context?.let { FirebaseApp.initializeApp(it) }
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}