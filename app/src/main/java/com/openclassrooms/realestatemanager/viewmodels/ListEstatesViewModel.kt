package com.openclassrooms.realestatemanager.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
        Firebase.database.setPersistenceEnabled(true)
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

    fun updatePhotosURIInSQLiteDB(auth: FirebaseAuth) {
        viewModelScope.launch {
            repositoryAccess.getPhotosURIFromCloudStorage(auth) { itPhoto, itLong ->
                viewModelScope.launch {
                    repositoryAccess.updatePhoto(itPhoto, itLong)
                }
            }
        }
    }

}
