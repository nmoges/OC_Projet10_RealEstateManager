package com.openclassrooms.data.service

import android.app.Activity
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.openclassrooms.data.R

object AutocompleteService {
    private const val AUTOCOMPLETE_REQUEST_CODE = 200

    /**
     * Performs an autocomplete request.
     */
    fun performAutocompleteRequest(activity: Activity) {
        val fields = listOf(Place.Field.ID, Place.Field.ADDRESS,
                                                Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                                 .build(activity)
        activity.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
