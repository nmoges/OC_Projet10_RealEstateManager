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
    val listEstates: MutableLiveData<List<Estate>> = MutableLiveData()

    /**
     * Contains selected [Estate].
     */
    var selectedEstate: MutableLiveData<Estate> = MutableLiveData()

    init {
        // TODO() : To update when Room database is implemented
        listEstates.value = DummyEstateGenerator.dummyListEstate
    }

    fun setSelectedEstate(position: Int) {
        selectedEstate.value = listEstates.value?.get(position)
    }
}
