package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * View Model class containing the currency value.
 */
class CurrencyViewModel: ViewModel() {

    private val _currencySelected: MutableLiveData<String> = MutableLiveData()
    val currencySelected: LiveData<String>
        get() = _currencySelected

    /**
     * Updates LiveData with new currency value.
     * @param currency : new currency value
     */
    fun updateCurrencySelected(currency: String) {
        _currencySelected.postValue(currency)
    }
}