package com.openclassrooms.realestatemanager.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Defines a custom [TextWatcher].
 */
abstract class CustomTextWatcher: TextWatcher {
    override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) { }
    override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) { }
    override fun afterTextChanged(sequence: Editable?) { }
}