package com.openclassrooms.realestatemanager.viewmodels

import android.content.Context
import android.widget.CheckBox
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.ui.fragments.FragmentSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SearchFiltersViewModel @Inject constructor(@ApplicationContext context: Context): ViewModel() {

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
    var availableStatus: Boolean = false  // true : available estates only
                                          // false : sold estates only

    /** Defines values of the "Points of interest" filter */
    var listPOIStatus: MutableList<Boolean> = mutableListOf()

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
}

