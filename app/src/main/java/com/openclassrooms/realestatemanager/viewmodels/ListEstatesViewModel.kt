package com.openclassrooms.realestatemanager.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.data.entities.FullEstateData
import com.openclassrooms.data.model.Agent
import com.openclassrooms.data.model.Estate
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.service.DummyListAgentGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View Model class containing the estates values.
 */
@HiltViewModel
class ListEstatesViewModel @Inject constructor(
    private val repositoryAccess: RealEstateRepositoryAccess): ViewModel() {

    /** Contains list of existing [Estate] objects.  */
    private val _listEstates: MutableLiveData<List<Estate>> = MutableLiveData()
    val listEstates: LiveData<List<Estate>>
        get() = _listEstates

    /** Contains a list of agents. */
    private val _listAgents: MutableLiveData<List<Agent>> = MutableLiveData()
    val listAgents: LiveData<List<Agent>>
        get() = _listAgents

    /** Contains selected [Estate]. */
    var selectedEstate: Estate? = null

    init {
        insertDummyListAgentInDb()
        restoreData()
    }

    // -------------------- Estate update --------------------
    /**
     * Set a selected [Estate] by user (click on item) to [selectedEstate]
     */
    fun setSelectedEstate(position: Int) { selectedEstate = listEstates.value?.get(position) }

    fun setResultInSelectedEstate(estate: Estate) {
        val list = listEstates.value
        var index = 0
        var found = false
        list?.let {
            while (!found && index < list.size) {
                if (estate.id == it[index].id) found = true
                else index++
            }
        }
        setSelectedEstate(index)
    }

    // -------------------- Data restoration --------------------
    /**
     * Restores data from database.
     */
    fun restoreData(): LiveData<List<FullEstateData>> = repositoryAccess.loadAllEstates()

    /**
     * Converts data from SQLite database into Estates.
     * @param list : list to convert
     */
    fun convertFullEstateInEstate(list: List<FullEstateData>) {
        viewModelScope.launch {
            _listEstates.postValue(repositoryAccess.convertListFullEstateDataToListEstate(list))
        }
    }
    /**
     * Restores the list of existing agents in database.
     */
    fun restoreListAgents() =
        viewModelScope.launch { _listAgents.postValue(repositoryAccess.getAllAgents()) }


    // -------------------- Autocomplete --------------------
    /**
     * Performs an autocomplete request.
     * @param activity : Main activity
     */
    fun performAutocompleteRequest(activity: Activity) =
        repositoryAccess.performAutocompleteRequest(activity)

    // -------------------- Dummy --------------------
    /**
     * Test method to insert fake date in table_agents from database.
     */
    private fun insertDummyListAgentInDb() {
        viewModelScope.launch {
            val nbAgents = repositoryAccess.getAllAgents().size
            if (nbAgents == 0) {
                DummyListAgentGenerator.listAgents.forEach {
                    repositoryAccess.insertAgent(it)
                }
            }
        }
    }

    // -------------------- NoSQL DB --------------------
    /**
     * Updates photos estates in NoSQL DB with URL.
     */
    fun updatePhotosURIInSQLiteDB() {
        viewModelScope.launch {
            // Send Photo to Cloud Storage
            val pair = repositoryAccess.getPhotosURIFromCloudStorage()
            for (i in pair.indices) {
                repositoryAccess.updatePhoto(pair[i].first, pair[i].second)
                // Send updated Estate to Realtime DB
                val indice = pair[i].second.toInt() - 1
                val updatedEstate = listEstates.value?.get(indice)
                updatedEstate?.let { estate ->
                    repositoryAccess.updateEstatePhotoInRealtimeDatabase(estate.firebaseId,
                        pair[i].first.uriConverted,
                        i.toString())
                }
            }
        }
    }
}
