package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.data.entities.EstateDataWithPhotosAndInterior
import com.openclassrooms.data.repository.RealEstateRepository
import com.openclassrooms.realestatemanager.Converters
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
import com.openclassrooms.realestatemanager.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListEstatesViewModel @Inject constructor(
    val repository: RealEstateRepository
): ViewModel() {

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

    /**
     * Set a selected [Estate] by user (click on item) to [selectedEstate]
     */
    fun setSelectedEstate(position: Int) { selectedEstate = listEstates.value?.get(position) }

    /**
     * Creates a new [Estate].
     */
    fun createNewEstate() {
        selectedEstate =
            Estate(id = 0, type = "", district = "", price = 0,
                   Interior(id= 0, numberRooms = 0, numberBathrooms = 0, numberBedrooms = 0, surface = 0),
                   description = "", address = "", nameAgent = "", status = false, selected = false)
    }

    fun updateDatabase(typeUpdate: Boolean) {
        val estate = selectedEstate ?: return
        if (!typeUpdate) insertEstateInDatabase(estate)
        else updateEstateInDatabase(estate)
    }

    /**
     * Creates a new [Photo] object for an [Estate]
     */
    fun createNewPhoto(namePhoto: String): Photo? {
        val uri = photoUriEstate.value
        return if (uri != null && uri.isNotEmpty()) { Photo(uri, namePhoto) } else null
    }

    fun updatePhotoUri(photoUri: String) = _photoUriEstate.postValue(photoUri)

    fun clearTempPhotoUri() = _photoUriEstate.postValue(null)

    /**
     * Removes photos added to an [Estate] if user cancelled its modifications.
     * @param numberPhotosAdded : Number of photos added since the beginning of the modifications.
     */
    fun removePhotosIfEstateCreationCancelled(numberPhotosAdded: Int) {
       var indice = numberPhotosAdded
        while (indice > 0) {
            selectedEstate?.listPhoto?.removeFirst()
            indice--
        }
    }

    // Database access
    fun restoreData(list: List<EstateDataWithPhotosAndInterior>) {
        val listEstateRestored: MutableList<Estate> = mutableListOf()

        list.forEach { it ->
            val interior: Interior = Converters.convertInteriorDataToInterior(it.interiorData)

            val listPhoto: MutableList<Photo> = mutableListOf()
            it.listPhotosData.forEach { listPhoto.add(Converters.convertPhotoDataToPhoto(it)) }

            val estate: Estate =
                            Converters.convertEstateDataToEstate(it.estateData, interior, listPhoto)

            listEstateRestored.add(estate)
        }
        _listEstates.postValue(listEstateRestored)
    }

    private fun insertEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = Converters.convertEstateToEstateData(estate)
            val id = repository.insertEstate(estateData)
            insertInteriorInDatabase(estate.interior, id)
            estate.listPhoto.forEach {
                insertPhotoInDatabase(it, id)
            }
        }
    }

    private fun insertPhotoInDatabase(photo: Photo, associatedId: Long) {
        viewModelScope.launch {
            val photoData = Converters.convertPhotoToPhotoData(photo, associatedId)
            repository.insertPhoto(photoData)
        }
    }

    private fun insertInteriorInDatabase(interior: Interior, associatedId: Long) {
        viewModelScope.launch {
            val interiorData = Converters.convertInteriorToInteriorData(interior, associatedId)
            repository.insertInterior(interiorData)
        }
    }

    private fun updateEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = Converters.convertEstateToEstateData(estate)
            // Update table_estates
            repository.updateEstate(estateData)
            // Update table_interiors
            updateInteriorInDatabase(estate.interior, estate.id)
            // Update table_photos
            val listPhotosInDb = repository.getPhotos(estate.id)
            if (estate.listPhoto.size > listPhotosInDb.size) { // At least one new photo added
                val nbPhotosAdded = estate.listPhoto.size - listPhotosInDb.size
                for (i in 0 until nbPhotosAdded) {
                    insertPhotoInDatabase(estate.listPhoto[i], estate.id)
                }
            }
        }
    }

    private fun updateInteriorInDatabase(interior: Interior, idAssociatedEstate: Long) {
        viewModelScope.launch {
            val interiorData = Converters.convertInteriorToInteriorData(interior, idAssociatedEstate)
            repository.updateInterior(interiorData)
        }
    }
}
