package com.openclassrooms.realestatemanager.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrencyViewModel: ViewModel() {

    private val _currencySelected: MutableLiveData<String> = MutableLiveData()
    val currencySelected: LiveData<String>
        get() = _currencySelected

    fun updateCurrencySelected(currency: String) {
        Log.i("CURRENCY", "updateCurrencySelected")
        _currencySelected.postValue(currency)
    }
}