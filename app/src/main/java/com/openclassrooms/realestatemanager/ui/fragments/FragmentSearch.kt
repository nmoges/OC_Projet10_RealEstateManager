package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding
import com.openclassrooms.realestatemanager.ui.LayoutInflaterProvider
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.ui.adapters.ListEstatesAdapter
import com.openclassrooms.realestatemanager.utils.DateComparator
import com.openclassrooms.realestatemanager.utils.StringHandler
import com.openclassrooms.realestatemanager.viewmodels.DialogsViewModel
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import com.openclassrooms.realestatemanager.viewmodels.SearchFiltersViewModel
import java.util.*

/**
 * [Fragment] subclass used to display search results.
 */
class FragmentSearch : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FragmentSearch()
        private fun Int.toPx(activity: MainActivity) =
            this * activity.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    /** View Binding parameter */
    private lateinit var binding: FragmentSearchBinding

    /** Contain [SearchFiltersViewModel] reference */
    private lateinit var searchFiltersViewModel: SearchFiltersViewModel

    /** Contain [ListEstatesViewModel] reference */
    private lateinit var listEstateViewModel: ListEstatesViewModel

    /** Contains a reference to a [DialogsViewModel] */
    private lateinit var dialogsViewModel: DialogsViewModel

    /** Contains a reference to an [AlertDialog] for search functionality. */
    private var builderSearchDialog: AlertDialog? = null

    /** Layout associated to the [builderSearchDialog] alert dialog. */
    private var viewLayoutDialog : View? = null

    /** [DatePickerDialog] for "start date" filter. */
    private var builderStartDatePickerDialog : DatePickerDialog? = null

    /** [DatePickerDialog] for "end date" filter. */
    private var builderEndDatePickerDialog: DatePickerDialog? = null

    /** Contains a reference to an [AlertDialog] for reset confirmation */
    private var builderConfirmReset: AlertDialog? = null

    /** Layout containing the list of "point of interest" tag filters*/
    private lateinit var linearLayoutTags: LinearLayout

    /** [RangeSlider] for "price" filtering. */
    private lateinit var rangeSliderPrice : RangeSlider

    /** [RangeSlider] for "surface" filtering. */
    private lateinit var rangeSliderSurface: RangeSlider

    /** [MaterialButton] for "Available estates" filtering */
    private lateinit var availableButton: MaterialButton

    /** [MaterialButton] for "Sold estates" filtering */
    private lateinit var soldButton: MaterialButton

    /** [TextInputEditText] containing a start date for filtering */
    private lateinit var textInputStartDate: TextInputEditText

    /** [TextInputLayout] associated with [textInputStartDate] */
    private lateinit var textLayoutStartDate: TextInputLayout

    /** [TextInputEditText] containing an end date for filtering */
    private lateinit var textInputEndDate: TextInputEditText

    /** [CheckBox] for "Price" filtering. */
    private lateinit var checkBoxPrice: CheckBox

    /** [CheckBox] for "Surface" filtering. */
    private lateinit var checkBoxSurface: CheckBox

    /** [CheckBox] for "Date" filtering. */
    private lateinit var checkBoxDate: CheckBox

    /** [CheckBox] for "Status estate" filtering. */
    private lateinit var checkBoxStatus: CheckBox

    /** [CheckBox] for "Points of interest" filtering. */
    private lateinit var checkBoxPOI: CheckBox

    /** List of existing "Point of interest" */
    private lateinit var listPOI: Array<out String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstateViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
        searchFiltersViewModel = ViewModelProvider(requireActivity())[SearchFiltersViewModel::class.java]
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listPOI = resources.getStringArray(R.array.poi)
        updateToolbarTitle()
        initializeRecyclerView()
        initializeSearchDialog()
        initializeConfirmationResetDialog()
        initializeSliders()
        handleRangeSliderPriceListener()
        handleRangeSliderSurfaceListener()
        initializeStatusMaterialButtons()
        initializeListPOISelectionInDialog()
        initializeDatePickerDialogs()
        checkDialogsStatusInViewModel()
        initializeTextInputDate()
        handleCheckboxPrice()
        handleCheckBoxSurface()
        handleCheckBoxDate()
        handleCheckBoxStatus()
        handleCheckBoxPOIStatus()
        handleDateTextInputClicks()
        handleMaterialButtonsStatus()
        handleMaterialButtonsPOI()
        handleRangeSlidersObservers()
        handleDateObserver()
        handleStatusObserver()
        handlePOIObserver()
        handleViewModelObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                dialogsViewModel.apply {
                    searchDialogStatus = false
                    confirmResetDialogStatus = false
                    startDatePickerDialogStatus = false
                    endDatePickerDialogStatus = false }
                resetAllFilters()
                resetSearchResults()
                (activity as MainActivity).onBackPressed() }
            R.id.filter -> {
                builderSearchDialog?.let {
                    dialogsViewModel.searchDialogStatus = true
                    it.show() }}
            R.id.reset -> {
                builderConfirmReset?.let {
                    dialogsViewModel.confirmResetDialogStatus = true
                    it.show() }}
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Initialize parent activity toolbar.
     */
    private fun updateToolbarTitle() {
        (activity as MainActivity).apply {
            setToolbarProperties(R.string.str_toolbar_fragment_search_title, true)
        }
    }

    /**
     * Initializes recycler view for list display.
     */
    private fun initializeRecyclerView() {
        binding.recyclerViewResults.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ListEstatesAdapter(context) { handleClickOnEstateItem(it) }
            (adapter as ListEstatesAdapter).activity = (activity as MainActivity)
        }
    }

    /**
     * Handles click events in list items.
     * @param position : item position
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleClickOnEstateItem(position: Int) {
        (binding.recyclerViewResults.adapter as ListEstatesAdapter).apply {
            clearPreviousSelection(position)
            val status: Boolean = updateItemSelectionStatus(position)
            notifyDataSetChanged()
            if (status) {
                activity.handleBackgroundGridVisibility(View.INVISIBLE)
                listEstates[position].selected = true
                listEstateViewModel.setResultInSelectedEstate(listEstates[position])
                activity.displayFragmentDetails()
            }
        }
    }

    /**
     * Clear "selected" status of the current selected item.
     */
    fun clearCurrentSelection() =
        (binding.recyclerViewResults.adapter as ListEstatesAdapter).clearCurrentSelection()

    /**
     * Resets all filters.
     */
    fun resetAllFilters() {
        context?.let { itContext ->
            searchFiltersViewModel.let {
                checkBoxPrice.isChecked = false
                checkBoxStatus.isChecked = false
                checkBoxSurface.isChecked = false
                checkBoxDate.isChecked = false
                checkBoxStatus.isChecked = false
                checkBoxPOI.isChecked = false
                it.restoreDefaultValues(itContext)
                it.resetCheckBoxes()
                if (textLayoutStartDate.isErrorEnabled) textLayoutStartDate.apply {
                        isErrorEnabled = false
                        error = "" }
            }
        }
    }

    /**
     * Update [MaterialTextView] displayed in background.
     * @param visibility : visibility status value
     */
    private fun handleBackgroundMaterialTextVisibility(visibility: Int) {
        binding.txtBackgroundNoResults.visibility = visibility
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderSearchDialog] property.
     */
    private fun initializeSearchDialog() {
        viewLayoutDialog = LayoutInflaterProvider.getViewFromLayoutInflater(R.layout.dialog_filters,
                                                                            context)
        viewLayoutDialog?.let {
            linearLayoutTags = it.findViewById(R.id.linear_layout_tags_filter)
            builderSearchDialog = AlertDialog.Builder(activity)
                                       .setTitle(R.string.str_dialog_search_title)
                                       .setView(it)
                                       .setPositiveButton(R.string.str_dialog_confirm) { _, _ ->
                                           performSearch()
                                       }
                                       .setNegativeButton(R.string.str_dialog_button_cancel) { _, _ -> }
                                       .create()
        }
    }

    /**
     * Initializes an [AlertDialog.Builder] for reset confirmation message display.
     */
    private fun initializeConfirmationResetDialog() {
        builderConfirmReset = AlertDialog.Builder(activity)
            .setTitle(R.string.str_dialog_reset_filters_title)
            .setMessage(R.string.str_dialog_reset_filters_message)
            .setPositiveButton(resources.getString(R.string.str_dialog_confirm)) { _, _ ->
                resetAllFilters()
                resetSearchResults()
            }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel))  { _, _ -> }
            .create()
    }
    /**
     * Initializes [RangeSlider] views.
     */
    private fun initializeSliders() {
        viewLayoutDialog?.let {
            // Initialize "Price" slider
            val valuesPrice = searchFiltersViewModel.valuesPrice
            val valuesSurface = searchFiltersViewModel.valuesSurface
            rangeSliderPrice = it.findViewById<RangeSlider>(R.id.range_slider_price).apply {
                this.setValues(valuesPrice[0].toFloat(), valuesPrice[1].toFloat())
                this.stepSize = valuesPrice[2].toFloat()
                updateMaterialTextRangeSlider(valuesPrice[0], valuesPrice[1],
                                                                     R.id.min_price, R.id.max_price)
            }
            rangeSliderSurface = it.findViewById<RangeSlider>(R.id.range_slider_surface).apply {
                this.setValues(valuesSurface[0].toFloat(), valuesSurface[1].toFloat())
                this.stepSize = valuesSurface[2].toFloat()
                updateMaterialTextRangeSlider(valuesSurface[0], valuesSurface[1],
                                                                 R.id.min_surface, R.id.max_surface)
            }
        }
    }

    /**
     * Add observe to [searchFiltersViewModel] "checkBoxPriceValue" and
     * "checkBoxSurfaceValue" LiveData properties.
     */
    private fun handleRangeSlidersObservers() {
        searchFiltersViewModel.checkBoxPriceValue.observe(viewLifecycleOwner, {
            val valuesPrice = searchFiltersViewModel.valuesPrice
            rangeSliderPrice.apply {
                isEnabled = it
                setValues(valuesPrice[0].toFloat(), valuesPrice[1].toFloat()) }
        })
        searchFiltersViewModel.checkBoxSurfaceValue.observe(viewLifecycleOwner, {
            val valuesSurface = searchFiltersViewModel.valuesSurface
            rangeSliderSurface.apply {
                isEnabled = it
                setValues(valuesSurface[0].toFloat(), valuesSurface[1].toFloat()) }
        })
    }

    /**
     * Add observe to [searchFiltersViewModel] "checkBoxDateValue" LiveData property.
     */
    private fun handleDateObserver() {
        searchFiltersViewModel.checkBoxDateValue.observe(viewLifecycleOwner, {
            textInputStartDate.isEnabled = it
            textInputStartDate.text = StringHandler.convertStringToEditable(searchFiltersViewModel.startDate)
            textInputEndDate.isEnabled = it
            textInputEndDate.text = StringHandler.convertStringToEditable(searchFiltersViewModel.endDate)
        })
    }

    /**
     * Add observe to [searchFiltersViewModel] "checkBoxStatusValue" LiveData property.
     */
    private fun handleStatusObserver() {
        searchFiltersViewModel.checkBoxStatusValue.observe(viewLifecycleOwner, {
            updateStatusMaterialButtons(it)
            availableButton.isClickable = it
            soldButton.isClickable = it })
    }

    /**
     * Add observe to [searchFiltersViewModel] "checkBoxPOIValue" LiveData property.
     */
    private fun handlePOIObserver() {
        searchFiltersViewModel.checkBoxPOIValue.observe(viewLifecycleOwner, {
            for (i in 0 until linearLayoutTags.childCount) {
                (linearLayoutTags.getChildAt(i) as MaterialButton).isClickable = it
                initializePOIMaterialButton((linearLayoutTags.getChildAt(i) as MaterialButton), i, it)
            } })
    }

    /**
     * Handles [searchFiltersViewModel] observer.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleViewModelObserver() {
        searchFiltersViewModel.searchResults.observe(viewLifecycleOwner, {
            (binding.recyclerViewResults.adapter as ListEstatesAdapter).apply {
                listEstates.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged() }
                val visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
                handleBackgroundMaterialTextVisibility(visibility)
            }
        })
    }

    /**
     * Initialize both [MaterialTextView] for "status" filter.
     */
    private fun initializeStatusMaterialButtons() {
        viewLayoutDialog?.let {
            availableButton = it.findViewById(R.id.button_available)
            soldButton = it.findViewById(R.id.button_sold) }
    }

    /**
     * Initializes the list of points of interest available for filter selection.
     */
    private fun initializeListPOISelectionInDialog() {
        // Initialize layoutParams for views to add in HorizontalScrollView
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply { marginStart = 20.toPx(activity as MainActivity) }
        context?.let { itContext ->
            // Add each button in HorizontalScrollView
            for (i in listPOI.indices) {
                val materialButton = MaterialButton(itContext).apply {
                    text = listPOI[i]
                    isAllCaps = false
                    isClickable = false }
                linearLayoutTags.addView(materialButton, layoutParams)
            }
        }
    }

    /**
     * Initializes all "tag" [MaterialButton] for "points of interest" filter.
     * @param button : "tag" button
     * @param indice : position in "listPOIStatus"
     * @param status : "enabled" button status
     */
    private fun initializePOIMaterialButton(button: MaterialButton, indice: Int, status: Boolean) {
        if (status)
            if (searchFiltersViewModel.listPOIStatus[indice])
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, button)
            else setColorsMaterialButton(R.color.white, R.color.colorPrimary, button)
        else setColorsMaterialButton(R.color.grey41, R.color.grey93, button)
    }

    /**
     * Initializes date dialogs.
     */
    private fun initializeDatePickerDialogs() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        context?.let {
            builderStartDatePickerDialog = DatePickerDialog(it, { _, year, month, dayOfMonth ->
                    updateTextInputEditTextWithDate(true, year, month, dayOfMonth)
                    checkTextInputEditTextFields()
                }, year, month, day)
            builderEndDatePickerDialog = DatePickerDialog(it, { _, year, month, dayOfMonth ->
                    updateTextInputEditTextWithDate(false, year, month, dayOfMonth)
                    checkTextInputEditTextFields()
            }, year, month, day)
        }
    }

    /**
     * Initializes start date and end date status.
     */
    private fun initializeTextInputDate() {
        viewLayoutDialog?.let {
            textLayoutStartDate = it.findViewById(R.id.text_layout_date_start)
            textInputStartDate = it.findViewById(R.id.text_input_date_start)
            textInputStartDate.apply {
                text = StringHandler.convertStringToEditable(searchFiltersViewModel.startDate) }
            textInputEndDate = it.findViewById(R.id.text_input_date_end)
            textInputEndDate.apply {
                text = StringHandler.convertStringToEditable(searchFiltersViewModel.endDate) }
        }
    }

    /**
     * Handles click events on "Price" [CheckBox].
     */
    private fun handleCheckboxPrice() {
        viewLayoutDialog?.let {
            checkBoxPrice = it.findViewById<CheckBox>(R.id.checkbox_price)
            checkBoxPrice.apply {
                this.isChecked = searchFiltersViewModel.checkBoxPriceStatus
                setOnCheckedChangeListener { _, isChecked ->
                    rangeSliderPrice.isEnabled = isChecked
                    searchFiltersViewModel.updateCheckBoxPriceValue(isChecked)
                }
            }
        }
    }

    /**
     * Handles click events on "Surface" [CheckBox].
     */
    private fun handleCheckBoxSurface() {
        viewLayoutDialog?.let {
            checkBoxSurface = it.findViewById<CheckBox>(R.id.checkbox_surface)
            checkBoxSurface.apply {
                this.isChecked = searchFiltersViewModel.checkBoxSurfaceStatus
                setOnCheckedChangeListener { _, isChecked ->
                    rangeSliderSurface.isEnabled = isChecked
                    searchFiltersViewModel.updateCheckBoxSurfaceValue(isChecked)
                }
            }
        }
    }

    /**
     * Handles click events on "Date" [CheckBox].
     */
    private fun handleCheckBoxDate() {
        viewLayoutDialog?.let {
            checkBoxDate = it.findViewById<CheckBox>(R.id.checkbox_date)
            checkBoxDate.apply {
                this.isChecked = searchFiltersViewModel.checkBoxDateStatus
                setOnCheckedChangeListener { _, isChecked ->
                    textInputStartDate.isEnabled = isChecked
                    textInputEndDate.isEnabled = isChecked
                    searchFiltersViewModel.updateCheckBoxDateValue(isChecked)
                }
            }
        }
    }

    /**
     * Handles click events on "Status" [CheckBox].
     */
    private fun handleCheckBoxStatus() {
        viewLayoutDialog?.let {
            checkBoxStatus = it.findViewById(R.id.checkbox_status)
            checkBoxStatus.apply {
                this.isChecked = searchFiltersViewModel.checkBoxTypeStatus
                setOnCheckedChangeListener { _, isChecked ->
                    updateStatusMaterialButtons(isChecked)
                    availableButton.isClickable = isChecked
                    soldButton.isClickable = isChecked
                    searchFiltersViewModel.updateCheckBoxStatusValue(isChecked)
                }
            }
        }
    }

    /**
     * Handles click events on "Points of interest" [CheckBox].
     */
    private fun handleCheckBoxPOIStatus() {
        viewLayoutDialog?.let { itView ->
            checkBoxPOI = itView.findViewById(R.id.checkbox_poi)
            checkBoxPOI.apply {
                this.isChecked = searchFiltersViewModel.checkBoxPOIStatus
                setOnCheckedChangeListener { _, isChecked ->
                    isChecked.let { itBoolean ->
                        for (i in 0 until linearLayoutTags.childCount) {
                            val materialButton = linearLayoutTags.getChildAt(i) as MaterialButton
                            if (itBoolean) {
                                materialButton.isClickable = true
                                if (searchFiltersViewModel.listPOIStatus[i])
                                    setColorsMaterialButton(R.color.colorPrimary,
                                                                      R.color.white, materialButton)
                                else setColorsMaterialButton(R.color.white,
                                                               R.color.colorPrimary, materialButton)
                            }
                            else {
                                materialButton.isClickable = false
                                setColorsMaterialButton(R.color.grey41, R.color.grey93, materialButton)
                            }
                        }
                        searchFiltersViewModel.updateCheckBoxPOIValue(itBoolean)
                    }
                }
            }
        }
    }

    /**
     * Handles "Price" slider interactions.
     */
    private fun handleRangeSliderPriceListener() {
        viewLayoutDialog?.let {
            val priceSlider = it.findViewById<RangeSlider>(R.id.range_slider_price)
            priceSlider.addOnChangeListener { slider, _, _ ->
                updateMaterialTextRangeSlider(slider.values[0].toInt(), slider.values[1].toInt(),
                                              R.id.min_price, R.id.max_price)
                searchFiltersViewModel.valuesPrice[0] = slider.values[0].toInt()
                searchFiltersViewModel.valuesPrice[1] = slider.values[1].toInt()
            }
        }
    }

    /**
     * Handles "Surface" slider interactions.
     */
    private fun handleRangeSliderSurfaceListener() {
        viewLayoutDialog?.let {
            val surfaceSlider = it.findViewById<RangeSlider>(R.id.range_slider_surface)
            surfaceSlider.addOnChangeListener { slider, _, _ ->
                updateMaterialTextRangeSlider(slider.values[0].toInt(), slider.values[1].toInt(),
                                              R.id.min_surface, R.id.max_surface)
                searchFiltersViewModel.valuesSurface[0] = slider.values[0].toInt()
                searchFiltersViewModel.valuesSurface[1] = slider.values[1].toInt()
            }
        }
    }

    /**
     * Handles click interactions with [textInputStartDate] and [textInputEndDate].
     */
    private fun handleDateTextInputClicks() {
        viewLayoutDialog?.let {
            textInputStartDate.setOnClickListener { builderStartDatePickerDialog?.show() }
            textInputEndDate.setOnClickListener { builderEndDatePickerDialog?.show() }
        }
    }

    /**
     * Handles click events on "available" and "sold" [MaterialButton]
     */
    private fun handleMaterialButtonsStatus() {
        viewLayoutDialog?.let {
            availableButton.setOnClickListener {
                searchFiltersViewModel.availableStatus = false
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, availableButton)
                setColorsMaterialButton(R.color.white, R.color.colorPrimary, soldButton)
            }
            soldButton.setOnClickListener {
                searchFiltersViewModel.availableStatus = true
                setColorsMaterialButton(R.color.white, R.color.colorPrimary, availableButton)
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, soldButton)
            }
        }
    }

    /**
     * Handles click events on all "tag" [MaterialButton]
     */
    private fun handleMaterialButtonsPOI() {
        for (i in 0 until linearLayoutTags.childCount) {
            (linearLayoutTags.getChildAt(i) as MaterialButton).setOnClickListener {
                // Update status
                searchFiltersViewModel.listPOIStatus[i] = !searchFiltersViewModel.listPOIStatus[i]
                // Update button properties
                if (searchFiltersViewModel.listPOIStatus[i])
                    setColorsMaterialButton(R.color.colorPrimary, R.color.white,
                                                   linearLayoutTags.getChildAt(i) as MaterialButton)
                else setColorsMaterialButton(R.color.white, R.color.colorPrimary,
                                                   linearLayoutTags.getChildAt(i) as MaterialButton)
            }
        }
    }

    /**
     * Updates [MaterialButton] status with associated color.
     * @param status : button status
     */
    private fun updateStatusMaterialButtons(status: Boolean) {
        if (status) { // Checkbox checked
            if (!searchFiltersViewModel.availableStatus) { // "Available" button selected
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, availableButton)
                setColorsMaterialButton(R.color.white, R.color.colorPrimary, soldButton)
            }
            else { // "Sold button selected
                setColorsMaterialButton(R.color.white, R.color.colorPrimary, availableButton)
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, soldButton)
            }
        }
        else { // Checkbox unchecked
            setColorsMaterialButton(R.color.grey41, R.color.grey93, availableButton)
            setColorsMaterialButton(R.color.grey41, R.color.grey93, soldButton)
        }
    }

    /**
     * Updates min and max values associated to a selected [RangeSlider].
     * @param minValue : min value to display
     * @param maxValue : max value to display
     * @param resMin : [MaterialTextView] resource displaying the min value
     * @param resMax : [MaterialTextView] resource displaying the max value
     */
    private fun updateMaterialTextRangeSlider(minValue: Int, maxValue: Int,
                                              resMin: Int, resMax: Int) {
        viewLayoutDialog?.let {
            it.findViewById<MaterialTextView>(resMin).apply {
                when(resMin) {
                    R.id.min_price -> {
                        val text = resources.getString(R.string.str_value_price_slider, minValue)
                        setText(text) }
                    R.id.min_surface -> {
                        val text = resources.getString(R.string.str_value_surface_slider, minValue)
                        setText(text) } }
            }
            it.findViewById<MaterialTextView>(resMax).apply {
                when(resMax) {
                    R.id.max_price -> {
                        val text = resources.getString(R.string.str_value_price_slider, maxValue)
                        setText(text) }
                    R.id.max_surface -> {
                        val text = resources.getString(R.string.str_value_surface_slider, maxValue)
                        setText(text) } }
            }
        }
    }

    /**
     * Updates [TextInputEditText] with selected date.
     * @param type : [textLayoutStartDate] if true, [textInputEndDate] if false
     * @param year : year value of a date
     * @param month : month value of a date
     * @param day : day value of a date
     */
    private fun updateTextInputEditTextWithDate(type: Boolean, year: Int, month: Int, day: Int) {
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, day)
        val currentDate = Utils.convertDateToFormat(selectedDate.time)
        if (type) {
            searchFiltersViewModel.startDate = currentDate
            textInputStartDate.text = StringHandler.convertStringToEditable(currentDate) }
        else {
            searchFiltersViewModel.endDate = currentDate
            textInputEndDate.text = StringHandler.convertStringToEditable(currentDate) }
    }

    /**
     * Checks [textLayoutStartDate] and [textInputEndDate] text values.
     */
    private fun checkTextInputEditTextFields() {
        if (textInputStartDate.text?.isNotEmpty() == true
            && textInputEndDate.text?.isNotEmpty() == true) {
            val status = DateComparator.compareDates(textInputStartDate.text.toString(),
                                                     textInputEndDate.text.toString())
            textLayoutStartDate.apply {
                isErrorEnabled = !status
                error = if (!status) "Error" else "" }
            builderSearchDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = status
        }
    }

    /**
     * Sets colors for text and background for a selected [MaterialButton]
     * @param textColor : color of the [MaterialButton] text
     * @param backgroundColor : color fo the [MaterialButton] background
     * @param button : [MaterialButton] to update
     */
    @Suppress("DEPRECATION")
    private fun setColorsMaterialButton(@ColorRes textColor: Int,
                                        @ColorRes backgroundColor: Int,
                                        button: MaterialButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            button.apply {
                setTextColor(resources.getColor(textColor, null))
                setBackgroundColor(resources.getColor(backgroundColor, null)) }
        else
            button.apply {
                setTextColor(resources.getColor(textColor))
                setBackgroundColor(resources.getColor(backgroundColor)) }
    }

    /**
     * Initializes filters to perform a search request.
     */
    private fun performSearch() {
        // Reset number of selected filters
        searchFiltersViewModel.nbFilters = 0
        // Initialize filters
        val surfaceFilter: ArrayList<Int?> = searchFiltersViewModel.initializeSurfaceFilter(
            minSurface = rangeSliderSurface.values[0].toInt(),
            maxSurface = rangeSliderSurface.values[1].toInt(),
            checkBoxStatus = checkBoxSurface.isChecked)
        val priceFilter: ArrayList<Int?> = searchFiltersViewModel.initializePriceFilter(
            minPrice = rangeSliderPrice.values[0].toInt(),
            maxPrice = rangeSliderPrice.values[1].toInt(),
            checkBoxStatus = checkBoxPrice.isChecked)
        val statusFilter: Boolean? = searchFiltersViewModel
                                    .initializeStatusFilter(checkBoxStatus.isChecked)
        val listPOIFilters: MutableList<String>? = searchFiltersViewModel.initializePOIFilter(
            checkBoxStatus = checkBoxPOI.isChecked,
            listPOI = listPOI)
        val datesFilter: ArrayList<String?> = searchFiltersViewModel.initializeDatesFilter(
            checkBoxStatus = checkBoxDate.isChecked,
            startDate = textInputStartDate.text,
            endDate = textInputEndDate.text)

        // Send request
        if (searchFiltersViewModel.nbFilters > 0) {
            searchFiltersViewModel
                .getSearchResultsFromRepository(priceFilter, surfaceFilter, statusFilter,
                                                                        listPOIFilters, datesFilter)
                .observe(viewLifecycleOwner,
                {
                    if (it.isNotEmpty()) searchFiltersViewModel.convertDataFromSearchRequest(it)
                    else resetSearchResults()
                })
        }
        else resetSearchResults()
    }

    /**
     * Resets search results displayed.
     */
    fun resetSearchResults() = searchFiltersViewModel.resetSearchResults()

    /**
     * Checks in [DialogsViewModel] if dialogs status before configuration change.
     */
    private fun checkDialogsStatusInViewModel() {
        if (dialogsViewModel.searchDialogStatus) builderSearchDialog?.show()
        if (dialogsViewModel.confirmResetDialogStatus) builderConfirmReset?.show()
        if (dialogsViewModel.startDatePickerDialogStatus) builderStartDatePickerDialog?.show()
        if (dialogsViewModel.endDatePickerDialogStatus) builderEndDatePickerDialog?.show()
    }

    override fun onPause() {
        saveInViewModels()
        super.onPause()
    }

    override fun onDestroy() {
        dismissDisplayedDialogs()
        super.onDestroy()
    }

    /**
     * Saves view values in view model.
     */
    private fun saveInViewModels() {
        // Dialogs status
        builderSearchDialog?.let { dialogsViewModel.searchDialogStatus = it.isShowing }
        builderConfirmReset?.let { dialogsViewModel.confirmResetDialogStatus = it.isShowing}
        builderStartDatePickerDialog?.let { dialogsViewModel.startDatePickerDialogStatus = it.isShowing }
        builderEndDatePickerDialog?.let { dialogsViewModel.endDatePickerDialogStatus = it.isShowing }
        // Checkbox status
        searchFiltersViewModel.apply {
            checkBoxPriceStatus = checkBoxPrice.isChecked
            checkBoxSurfaceStatus = checkBoxSurface.isChecked
            checkBoxDateStatus = checkBoxStatus.isChecked
            checkBoxTypeStatus = checkBoxStatus.isChecked
            checkBoxPOIStatus = checkBoxPOI.isChecked }
    }

    /**
     * Dismiss displayed dialogs before configuration change.
     */
    private fun dismissDisplayedDialogs() {
        builderSearchDialog?.let { if (it.isShowing) it.dismiss() }
        builderConfirmReset?.let { if (it.isShowing) it.dismiss() }
        builderStartDatePickerDialog?.let { if (it.isShowing) it.dismiss() }
        builderEndDatePickerDialog?.let { if (it.isShowing) it.dismiss() }
    }
}