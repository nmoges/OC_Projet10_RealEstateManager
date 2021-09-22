package com.openclassrooms.realestatemanager.viewmodels

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.data.model.*
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EstateViewModel @Inject constructor(
    private val repositoryAccess: RealEstateRepositoryAccess): ViewModel() {

    /** Temporary value for type of operation (estate/update creation) */
    var typeOperation:  Boolean = false

    /** Contains temporary [Estate] object */
    lateinit var estate: Estate

    /** Contains temporary number of photo added by user */
    var numberPhotosAdded: Int = 0

    /** Contains temporary uri value */
    var photoUriEstate: String = ""

    /** Contains a temporary list of points of interest */
    val listPOI: MutableList<String> = mutableListOf()

    /** Contains temporary agent id selected in dialog*/
    var idAgentSelected: Long = 1

    /** Temporary value storing the error display status for sliders */
    var errorSlidersStatus: Boolean = false

    /** Temporary value storing the position in the list of agents */
    var nameAgentSelected: String = ""

    init { resetEstate() }

    /**
     * Creates a new [Estate].
     */
    private fun createNewEstate(): Estate = Estate(
                id = 1, type = "", price = 0, description = "", status = false, selected = false,
                location = Location(id = 1, address = "", district = "",
                                    latitude = 0.0, longitude = 0.0),
                interior = Interior(id= 1, numberRooms = 5, numberBathrooms = 1,
                                    numberBedrooms = 1, surface = 50),
                agent = Agent(id = 1, firstName = "", lastName = ""),
                dates = Dates(id = 1, dateEntry = "", dateSale = "")
    )

    /**
     * Reset [estate] and [listPOI] values.
     */
    fun resetEstate() {
        estate = createNewEstate()
        listPOI.clear()
    }

    /**
     * Initializes [estate] with [selectedEstate] values (Update estate).
     * @param selectedEstate : Estate to modify
     */
    fun initializeWithSelectedEstateValues(selectedEstate: Estate) {
        estate = selectedEstate
        estate.listPointOfInterest.forEach { listPOI.add(it.name) }
    }

    /**
     * Removes photos added to an [Estate] if user cancelled its modifications.
     */
    fun removePhotosIfEstateCreationCancelled() {
        while (numberPhotosAdded > 0) {
            estate.listPhoto.removeLast()
            numberPhotosAdded--
        }
    }

    /**
     * Creates a new [Photo] object for an [Estate]
     */
    fun createNewPhoto(namePhoto: String): Photo? {
        val uri = photoUriEstate
        return if (uri.isNotEmpty()) { Photo(uri, namePhoto) } else null
    }

    /**
     * Updates location value.
     * @param place : Place returned from an autocomplete request
     * @param context : Context
     */
    fun updateLocationSelectedEstate(place : Place, context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        place.latLng?.let {
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            if (addresses.isNotEmpty()) {
                val district = addresses.first().subLocality ?: addresses.first().locality
                estate.location.apply {
                    this.latitude = it.latitude
                    this.longitude = it.longitude
                    this.address = place.address ?: ""
                    this.district = district
                }
            }
        }
    }

    /**
     * Adds date to selected estate.
     * @param type : type of date to add (entry date or sale date)
     */
    fun updateDateEstate(type: Boolean) {
        if (!type) estate.dates.dateEntry = Utils.getDate()
        else estate.dates.dateSale = Utils.getDate()
    }

    /**
     * Returns a LiveData containing the new estate/updated estate to send to database.
     * @return : LiveData<Estate>
     */
    fun getNewEstate(auth: FirebaseAuth): LiveData<Estate> {
        val newEstate = MutableLiveData<Estate>()
        viewModelScope.launch {
            // Update agent
            val agent = repositoryAccess.getAgentById(idAgentSelected)
            estate.agent.apply {
                this.id = agent.id
                this.firstName = agent.firstName
                this.lastName = agent.lastName
            }
            // Add entry date
            updateDateEstate(false)
            // Add list Points of interest
            estate.listPointOfInterest.clear()
            for (i in 0 until listPOI.size) {
                estate.listPointOfInterest.add(PointOfInterest(i.toLong(), listPOI[i]))
            }
            listPOI.clear()
            // Send photos to cloud Storage
            if (numberPhotosAdded > 0) {
                for (i in estate.listPhoto.size-numberPhotosAdded until estate.listPhoto.size) {
                    repositoryAccess.sendPhotosToCloudStorage(estate.listPhoto[i], auth) { itURL ->
                        estate.listPhoto[i].uriConverted = itURL
                        if (i == estate.listPhoto.size-1 && estate.listPhoto[i].uriConverted == itURL) {
                            numberPhotosAdded = 0
                            newEstate.postValue(estate)
                        }
                    }
                }
            }
            else newEstate.postValue(estate)
        }
        return newEstate
    }

}