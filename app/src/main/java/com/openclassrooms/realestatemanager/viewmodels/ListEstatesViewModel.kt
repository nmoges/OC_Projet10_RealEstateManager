package com.openclassrooms.realestatemanager.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
import com.openclassrooms.realestatemanager.model.Photo
import com.openclassrooms.realestatemanager.service.DummyEstateGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ListEstatesViewModel : ViewModel() {

    /**
     * Contains list of existing [Estate] objects.
     */
    private val _listEstates: MutableLiveData<List<Estate>> = MutableLiveData()
    val listEstates: LiveData<List<Estate>>
        get() = _listEstates

    private val _photoUriEstate: MutableLiveData<String> = MutableLiveData()
    val photoUriEstate: LiveData<String>
        get() = _photoUriEstate

    /**
     * Contains selected [Estate].
     */
    var selectedEstate: Estate? = null


    init {
        // TODO() : To update when Room database is implemented
        _listEstates.value = DummyEstateGenerator.dummyListEstate
    }

    /**
     * Set a selected [Estate] by user (click on item) to [selectedEstate]
     */
    fun setSelectedEstate(position: Int) {
        selectedEstate = listEstates.value?.get(position)
    }

    /**
     * Creates a new [Estate].
     */
    fun createNewEstate() {
        selectedEstate = _listEstates.value?.size?.let {
            Estate(id = it, type = "", district = "", price = 0,
                   Interior(numberRooms = 0, numberBathrooms = 0, numberBedrooms = 0, surface = 0),
                   description = "", address = "", nameAgent = "", status = false, selected = false)
        }
    }

    fun updateViewModel(typeUpdate: Boolean) {
        val estate = selectedEstate ?: return
        if (!typeUpdate) { // Existing Estate
            DummyEstateGenerator.dummyListEstate.add(estate)
        }
        _listEstates.postValue(DummyEstateGenerator.dummyListEstate)
    }

    /**
     * Creates a new [Photo] object for an [Estate]
     */
    fun createNewPhoto(namePhoto: String): Photo? {
        val uri = photoUriEstate.value
        return if (uri != null && uri.isNotEmpty()) { Photo(uri, namePhoto) } else null
    }

    fun updatePhotoUri(photoUri: String) {
        _photoUriEstate.postValue(photoUri)
    }

    fun clearTempPhotoUri() {
        _photoUriEstate.postValue(null)
    }
}
