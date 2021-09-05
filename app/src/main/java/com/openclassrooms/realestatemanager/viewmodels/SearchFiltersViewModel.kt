package com.openclassrooms.realestatemanager.viewmodels

import android.content.Context
import android.text.Editable
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.data.entities.FullEstateData
import com.openclassrooms.data.repository.RealEstateRepositoryAccess
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.data.model.Estate
import com.openclassrooms.realestatemanager.ui.fragments.FragmentSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View Model class containing the search results.
 */
@HiltViewModel
class SearchFiltersViewModel @Inject constructor(@ApplicationContext context: Context,
    private val repositoryAccess: RealEstateRepositoryAccess): ViewModel() {

    /** Contains boolean value for "Price" [CheckBox] */
    var checkBoxPriceValue: MutableLiveData<Boolean> = MutableLiveData(false)

    /** Contains boolean value for "Surface" [CheckBox] */
    var checkBoxSurfaceValue: MutableLiveData<Boolean> = MutableLiveData(false)

    /** Contains boolean value for "Date" [CheckBox] */
    var checkBoxDateValue: MutableLiveData<Boolean> = MutableLiveData(false)

    /** Contains boolean value for "Status" [CheckBox] */
    var checkBoxStatusValue: MutableLiveData<Boolean> = MutableLiveData(false)

    /** Contains boolean value for "Points of interest" [CheckBox] */
    var checkBoxPOIValue: MutableLiveData<Boolean> = MutableLiveData(false)

    /** Contains min and max values for "Price" [RangeSlider] */
    var valuesPrice: IntArray = context.resources.getIntArray(R.array.slider_price_values)

    /** Contains min and max values for "Surface" [RangeSlider] */
    var valuesSurface: IntArray = context.resources.getIntArray(R.array.slider_surface_values)

    /** Contains string value of "start Date" [TextInputEditText] field */
    var startDate: String = ""

    /** Contains string value of "end Date" [TextInputEditText] field */
    var endDate: String = ""

    /** Defines value of the "Estate status" filter */
    var availableStatus: Boolean = false  // false : available estates only
                                          // true : sold estates only

    /** Defines values of the "Points of interest" filter */
    var listPOIStatus: MutableList<Boolean> = mutableListOf()

    private val _searchResults: MutableLiveData<List<Estate>> = MutableLiveData()
    val searchResults: LiveData<List<Estate>>
        get() = _searchResults

    var nbFilters = 0

    init {
        // Initialize listPOIStatus with default values (no filter selected)
        initializeListPOIStatus(context)
    }

    /**
     * Initializes list of "points of interest" filters status selection.
     */
    private fun initializeListPOIStatus(context: Context) {
        val list: Array<out String> = context.resources.getStringArray(R.array.poi)
        for (i in list.indices) { listPOIStatus.add(false) }
    }

    /**
     * Updates [MutableLiveData] [checkBoxPriceValue].
     * @param value : boolean value to post
     */
    fun updateCheckBoxPriceValue(value: Boolean) = checkBoxPriceValue.postValue(value)

    /**
     * Updates [MutableLiveData] [checkBoxSurfaceValue].
     * @param value : boolean value to post
     */
    fun updateCheckBoxSurfaceValue(value: Boolean) = checkBoxSurfaceValue.postValue(value)

    /**
     * Updates [MutableLiveData] [checkBoxDateValue].
     * @param value : boolean value to post
     */
    fun updateCheckBoxDateValue(value: Boolean) = checkBoxDateValue.postValue(value)

    /**
     * Updates [MutableLiveData] [checkBoxStatusValue].
     * @param value : boolean value to post
     */
    fun updateCheckBoxStatusValue(value: Boolean) = checkBoxStatusValue.postValue(value)

    /**
     * Updates [MutableLiveData] [checkBoxPOIValue].
     * @param value : boolean value to post
     */
    fun updateCheckBoxPOIValue(value: Boolean) = checkBoxPOIValue.postValue(value)

    /**
     * Restores default values fo [FragmentSearch] dialog views.
     * @param context : context
     */
    fun restoreDefaultValues(context: Context) {
        valuesPrice = context.resources.getIntArray(R.array.slider_price_values)
        valuesSurface = context.resources.getIntArray(R.array.slider_surface_values)
        startDate = ""
        endDate = ""
        availableStatus = false
        listPOIStatus.clear()
        initializeListPOIStatus(context)
    }

    /**
     * Resets all [MutableLiveData] values.
     */
    fun resetCheckBoxes() {
        updateCheckBoxPriceValue(false)
        updateCheckBoxSurfaceValue(false)
        updateCheckBoxDateValue(false)
        updateCheckBoxStatusValue(false)
        updateCheckBoxPOIValue(false)
    }

    /**
     * Uses [RealEstateRepositoryAccess] interface to perform search requests.
     * @param price : Contains min and max price values for price filtering
     * @param surface : Contains min and max surface values for surface filtering
     * @param status : Contains boolean value defining the status estate filtering
     * @param dates : Contains dates values for date filtering
     * @return : Request results
     */
    fun getSearchResultsFromRepository(price: ArrayList<Int?>, surface: ArrayList<Int?>,
                                       status: Boolean?, listPoi : MutableList<String>?,
                                       dates: ArrayList<String?>): LiveData<List<FullEstateData>> {
        return repositoryAccess.getSearchResults(price, surface, status, dates, listPoi, nbFilters)
    }

    /**
     * Converts all [FullEstateData] objects returned from search request into a list [Estate] objects
     * to store in [_searchResults] property.
     * @param searchResults : Request results to convert
     */
    fun convertDataFromSearchRequest(searchResults: List<FullEstateData>) {
        viewModelScope.launch {
            _searchResults.postValue(repositoryAccess.convertListFullEstateDataToListEstate(searchResults))
        }
    }

    /**
     * Reset list of stored results.
     */
    fun resetSearchResults() {
        _searchResults.postValue(mutableListOf())
    }

    /**
     * Initializes a "Surface" filter for SQL search requests.
     * @param minSurface : Min surface value to search
     * @param maxSurface : Max surface value to search
     * @param checkBoxStatus : Filter activation status
     * @return : "Surface" filter
     */
    fun initializeSurfaceFilter(minSurface: Int, maxSurface: Int, checkBoxStatus: Boolean): ArrayList<Int?> {
        var minSurfaceFilter: Int? = null
        var maxSurfaceFilter: Int? = null
        if (checkBoxStatus) {
            minSurfaceFilter = minSurface
            maxSurfaceFilter= maxSurface
            nbFilters++
        }
        val surfaceFilter: ArrayList<Int?> = arrayListOf()
        surfaceFilter.apply {
            add(minSurfaceFilter)
            add(maxSurfaceFilter)
        }
        return surfaceFilter
    }

    /**
     * Initializes a "Price" filter for SQL search requests.
     * @param minPrice : Min price value to search
     * @param maxPrice : Max price value to search
     * @param checkBoxStatus : Filter activation status
     * @return : "Price" filter
     */
    fun initializePriceFilter(minPrice: Int, maxPrice: Int, checkBoxStatus: Boolean): ArrayList<Int?> {
        var minPriceFilter: Int? =  null
        var maxPriceFilter: Int? =  null
        if (checkBoxStatus) {
            minPriceFilter = minPrice
            maxPriceFilter = maxPrice
            nbFilters++
        }
        val priceFilter: ArrayList<Int?> = arrayListOf()
        priceFilter.apply {
            add(minPriceFilter)
            add(maxPriceFilter)
        }
        return priceFilter
    }

    /**
     * Initializes an "Estate status" filter for SQL search requests.
     * @param checkBoxStatus : Filter activation status
     * @return : "Estate Status" filter
     */
    fun initializeStatusFilter(checkBoxStatus: Boolean): Boolean? {
        if (checkBoxStatus) nbFilters++
        return if (checkBoxStatus) availableStatus else null
    }

    /**
     * Initializes a "Points of interest" filter for SQL search requests.
     * @param checkBoxStatus : Filter activation status
     * @param listPOI : List of existing points of interest
     * @return : "Point of interest" filter
     */
    fun initializePOIFilter(checkBoxStatus: Boolean, listPOI: Array<out String>): MutableList<String>? {
        var listPOIFilters: MutableList<String>? = null
        if (checkBoxStatus && listPOIStatus.contains(true)) {
            listPOIFilters = mutableListOf()
            for (i in 0 until listPOIStatus.size) {
                if (listPOIStatus[i]) listPOIFilters.add(listPOI[i])
            }
            nbFilters++
        }
        return listPOIFilters
    }

    /**
     * Initializes a "Points of interest" filter for SQL search requests.
     * @param checkBoxStatus : Filter activation status
     * @param startDate : Defines min date
     * @param endDate : Define max date
     * @return : "Dates" filter
     */
    fun initializeDatesFilter(checkBoxStatus: Boolean, startDate: Editable?, endDate: Editable?): ArrayList<String?> {
        var startDateFilter: String? = null
        var endDateFilter: String? = null
        val datesFilter: ArrayList<String?> = arrayListOf()

        if (checkBoxStatus && startDate?.isNotEmpty() == true && endDate?.isNotEmpty() == true) {
            startDateFilter = Utils.convertStringToSQLiteFormat(startDate.toString())
            endDateFilter = Utils.convertStringToSQLiteFormat(endDate.toString())
            nbFilters++
        }
        datesFilter.apply {
            add(startDateFilter)
            add(endDateFilter)
        }
        return datesFilter
    }
}

