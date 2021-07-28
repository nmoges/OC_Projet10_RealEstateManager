package com.openclassrooms.realestatemanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base application class for Hilt dependency injection.
 */
@HiltAndroidApp
class BaseApplication: Application()