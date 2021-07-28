package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.data.entities.EstateDataWithPhotosAndInterior
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.*
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Interior
import com.openclassrooms.realestatemanager.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListEstatesViewModel @Inject constructor(
    val repositoryAccess: RealEstateRepositoryAccess
): ViewModel() {
    /**
     * Contains list of existing [Estate] objects.
     */
    private val _listEstates: MutableLiveData<List<Estate>> = MutableLiveData()
    val listEstates: LiveData<List<Estate>>
        get() = _listEstates

    /**
     * Contains a temporary photo uri converted value.
     */
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

    /**
     * Determines if database operation is an insertion or an update.
     * @param typeUpdate : type of operation in database
     */
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

    /**
     * Adds new uri value.
     * @param photoUri : converted uri
     */
    fun updatePhotoUri(photoUri: String) = _photoUriEstate.postValue(photoUri)

    /**
     * Clears temporary uri values.
     */
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
    /**
     * Restores data from database.
     * @param list : list of data
     */
    fun restoreData(list: List<EstateDataWithPhotosAndInterior>) {
        val listEstateRestored: MutableList<Estate> = mutableListOf()

        list.forEach { it ->
            val interior = it.interiorData.toInterior()
            val listPhoto: MutableList<Photo> = mutableListOf()
            it.listPhotosData.forEach {
                listPhoto.add(it.toPhoto())
            }
            val estate = it.estateData.toEstate(interior, listPhoto)
            listEstateRestored.add(estate)
        }
        _listEstates.postValue(listEstateRestored)
    }

    /**
     * Access insert estate method from repository interface.
     * @param estate : estate data to store in table_photos
     */
    private fun insertEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = estate.toEstateData()
            val id = repositoryAccess.insertEstate(estateData)
            insertInteriorInDatabase(estate.interior, id)
            estate.listPhoto.forEach {
                insertPhotoInDatabase(it, id)
            }
        }
    }

    /**
     * Access insert photo method from repository interface.
     ** @param photo : Photo data to store in table_photos
     * @param associatedId : associated estate id
     */
    private fun insertPhotoInDatabase(photo: Photo, associatedId: Long) {
        viewModelScope.launch {
            val photoData = photo.toPhotoData(associatedId)
            repositoryAccess.insertPhoto(photoData)
        }
    }

    /**
     * Access insert interior method from repository interface.
     * @param interior : interior data to store in table_interiors
     * @param associatedId : associated estate id
     */
    private fun insertInteriorInDatabase(interior: Interior, associatedId: Long) {
        viewModelScope.launch {
            val interiorData = interior.toInteriorData(associatedId)
            repositoryAccess.insertInterior(interiorData)
        }
    }

    /**
     * Updates table_estates from database.
     * @param estate : updated estate
     */
    private fun updateEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = estate.toEstateData()
            // Update table_estates
            repositoryAccess.updateEstate(estateData)
            // Update table_interiors
            updateInteriorInDatabase(estate.interior, estate.id)
            // Update table_photos
            val listPhotosInDb = repositoryAccess.getPhotos(estate.id)
            if (estate.listPhoto.size > listPhotosInDb.size) { // At least one new photo added
                for (i in listPhotosInDb.size until estate.listPhoto.size) {
                    insertPhotoInDatabase(estate.listPhoto[i], estate.id)
                }
            }
        }
    }

    /**
     * Updates table_interiors from database.
     * @param interior : updated interior
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateInteriorInDatabase(interior: Interior, idAssociatedEstate: Long) {
        viewModelScope.launch {
            val interiorData = interior.toInteriorData(idAssociatedEstate)
            repositoryAccess.updateInterior(interiorData)
        }
    }
}
