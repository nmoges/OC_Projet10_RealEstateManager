package com.openclassrooms.realestatemanager.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.service.DummyEstateGenerator

class ListEstatesViewModel : ViewModel() {

    /**
     * Contains list of existing [Estate] objects.
     */
    private val _listEstates: MutableLiveData<List<Estate>> = MutableLiveData()
    val listEstates: LiveData<List<Estate>>
        get() = _listEstates

    /**
     * Contains selected [Estate].
     */
    private val _selectedEstate: MutableLiveData<Estate> = MutableLiveData()
    val selectedEstate: LiveData<Estate>
        get() = _selectedEstate

    init {
        // TODO() : To update when Room database is implemented
        _listEstates.value = DummyEstateGenerator.dummyListEstate
    }

    fun setSelectedEstate(position: Int) {
        _selectedEstate.value = listEstates.value?.get(position)
    }
}
