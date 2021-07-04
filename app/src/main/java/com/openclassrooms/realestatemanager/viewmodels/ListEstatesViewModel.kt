package com.openclassrooms.realestatemanager.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
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

    /**
     * Set a selected [Estate] by user (click on item) to [_selectedEstate]
     */
    fun setSelectedEstate(position: Int) {
        _selectedEstate.value = listEstates.value?.get(position)
    }

    fun createNewEstate() {
        _selectedEstate.value = _listEstates.value?.size?.let {
            Estate(
                id = it,
                type = "",
                district = "",
                price = 0,
                Interior(numberRooms = 0, numberBathrooms = 0,
                    numberBedrooms = 0, surface = 0),
                description = "",
                address = "",
                nameAgent = "",
                status = false,
                selected = false)
        }
    }

    fun updateViewModel(estate: Estate, typeUpdate: Boolean) {
        if (typeUpdate) { // Existing Estate
            DummyEstateGenerator.dummyListEstate[estate.id] = estate
        }
        else { // New Estate
            DummyEstateGenerator.dummyListEstate.add(estate)
        }
        _listEstates.postValue(DummyEstateGenerator.dummyListEstate)
    }
}
