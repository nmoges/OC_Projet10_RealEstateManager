package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.data.entities.EstateDataWithPhotosAndInterior
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.model.*
import com.openclassrooms.realestatemanager.model.date.Dates
import com.openclassrooms.realestatemanager.model.date.EntryDate
import com.openclassrooms.realestatemanager.model.date.SaleDate
import com.openclassrooms.realestatemanager.service.DummyListAgentGenerator
import com.openclassrooms.realestatemanager.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * View Model class containing the estates values.
 */
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

    private val _listAgents: MutableLiveData<List<Agent>> = MutableLiveData()
    val listAgents: LiveData<List<Agent>>
        get() = _listAgents

    /**
     * Contains selected [Estate].
     */
    var selectedEstate: Estate? = null

    init { restoreListAgents() }

    /**
     * Restores the list of existing agents in database.
     */
    private fun restoreListAgents() {
        viewModelScope.launch {
            _listAgents.postValue(getAllAgents())
        }
    }

    /**
     * Set a selected [Estate] by user (click on item) to [selectedEstate]
     */
    fun setSelectedEstate(position: Int) { selectedEstate = listEstates.value?.get(position) }

    /**
     * Creates a new [Estate].
     */
    fun createNewEstate() {
        selectedEstate =
            Estate(id = 1, type = "", district = "", price = 0, description = "", address = "",
                   interior = Interior(id= 1, numberRooms = 0, numberBathrooms = 0,
                                       numberBedrooms = 0, surface = 0),
                   agent = Agent(id = 1, firstName = "", lastName = ""),
                   status = false, selected = false,
                   dates = Dates(id = 1, entryDate = EntryDate(), saleDate = SaleDate())
            )
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
            selectedEstate?.listPhoto?.removeLast()
            indice--
        }
    }

    /**
     * Restores data from database.
     * @param list : list of data
     */
    fun restoreData(list: List<EstateDataWithPhotosAndInterior>) {
        viewModelScope.launch {
            val listEstateRestored: MutableList<Estate> = mutableListOf()
            list.forEach { it ->
                val interior = it.interiorData.toInterior()
                val listPhoto: MutableList<Photo> = mutableListOf()
                it.listPhotosData.forEach { listPhoto.add(it.toPhoto()) }
                val agentData = repositoryAccess.getAgentById(it.estateData.idAgent)
                val datesData = repositoryAccess.getDatesById(it.estateData.idEstate)
                val estate = it.estateData.toEstate(interior, listPhoto,
                                                    agentData.toAgent(), datesData.toDates())
                listEstateRestored.add(estate)
            }
            _listEstates.postValue(listEstateRestored)
        }
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
            insertDatesInDatabase(estate.dates, id)
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
     * Access insert dates method from repository interface.
     * @param dates : dates data to store in table_dates
     * @param associatedId : associated estate id
     */
    private fun insertDatesInDatabase(dates: Dates, associatedId: Long) {
        viewModelScope.launch {
            val datesData = dates.toDatesData(associatedId)
            repositoryAccess.insertDates(datesData)
        }
    }

    /**
     * Updates table_estates from database.
     * @param estate : updated estate
     */
    private fun updateEstateInDatabase(estate: Estate) {
        viewModelScope.launch {
            val estateData = estate.toEstateData()
            estateData.idEstate = estate.id
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
            // Update table_dates
            updateDatesInDatabase(estate.dates, estate.id)
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
            interiorData.idInterior = idAssociatedEstate
            repositoryAccess.updateInterior(interiorData)
        }
    }

    /**
     * Updates table_dates from database.
     * @param dates : updated dates
     * @param idAssociatedEstate : estate associated id
     */
    private fun updateDatesInDatabase(dates: Dates, idAssociatedEstate: Long) {
        viewModelScope.launch {
            val datesData = dates.toDatesData(idAssociatedEstate)
            datesData.idDates = idAssociatedEstate
            repositoryAccess.updateDates(datesData)
        }
    }

    // TODO() : test function to remove later : used to inject dummy agent data
    fun test() {
        viewModelScope.launch {
            DummyListAgentGenerator.listAgents.forEach {
                repositoryAccess.insertAgent(it.toAgentData())
            }
            restoreListAgents()
        }
    }

    /**
     * Retrieves all stored agents data from database.
     */
    private suspend fun getAllAgents(): List<Agent> {
        val listAgentData = repositoryAccess.getAllAgents()
        val listAgents: MutableList<Agent> = mutableListOf()
        listAgentData.forEach {
            listAgents.add(it.toAgent())
        }
        return listAgents
    }

    /**
     * Update [selectedEstate] field with [Agent] values.
     * @param id : selected id agent
     * @param updateEstate : defines type of operation (creation or update estate)
     */
    fun addAgentToSelectedEstate(id: Int, updateEstate: Boolean) {
        viewModelScope.launch {
            val agentData = repositoryAccess.getAgentById(id.toLong())
            val agent = agentData.toAgent()
            selectedEstate?.agent.apply {
                this?.id = agent.id
                this?.firstName = agent.firstName
                this?.lastName = agent.lastName
            }
            updateDatabase(updateEstate)
        }
    }

    /**
     * Updates [selectedEstate] field with [Interior] values.
     * @param numberRooms : number of rooms
     * @param numberBathrooms : number of bathrooms
     * @param numberBedrooms : number of bedrooms
     * @param surface : surface estate
     */
    fun updateInteriorSelectedEstate(numberRooms: Int, numberBathrooms: Int,
                                     numberBedrooms: Int, surface: Int ) {
        selectedEstate?.interior.apply {
            this?.numberRooms = numberRooms
            this?.numberBedrooms = numberBedrooms
            this?.numberBathrooms = numberBathrooms
            this?.surface = surface
        }
    }

    /**
     * Updates [selectedEstate] field values.
     * @param type : type of estate
     * @param location : address of the estate
     * @param description : description of the estate
     * @param price : price of the estate
     */
    fun updateSelectedEstate(type: String, location: String, description: String, price: Int) {
        selectedEstate?.apply {
            this.type = type
            this.address = location
            this.description = description
            this.price = price
        }
    }

    /**
     * Adds date to selected estate.
     * @param type : type of date to add (entry date or sale date)
     */
    fun addDateToSelectedEstate(type: Boolean) {
        val calendar: Calendar = Calendar.getInstance()
        val date: MutableList<Int> = mutableListOf(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH)+1,
            calendar.get(Calendar.YEAR)
        )
        if (!type) selectedEstate?.dates?.entryDate = EntryDate(
                          day = date[0], month = date[1], year = date[2])
        else selectedEstate?.dates?.saleDate = SaleDate(
                          day = date[0], month = date[1], year = date[2])
    }
}

