package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.ui.LayoutInflaterProvider
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.DateComparator
import com.openclassrooms.realestatemanager.utils.StringHandler
import java.util.*

class FragmentSearch : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FragmentSearch()

        private fun Int.toPx(activity: MainActivity) =
            this * activity.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

    /** Contains a reference to an [AlertDialog] for search functionality. */
    private lateinit var builderSearchDialog: AlertDialog

    /** Layout associated to the [builderSearchDialog] alert dialog. */
    private var viewLayoutDialog : View? = null

    /** [DatePickerDialog] for "start date" filter. */
    private lateinit var builderStartDatePickerDialog : DatePickerDialog

    /** [DatePickerDialog] for "end date" filter. */
    private lateinit var builderEndDatePickerDialog: DatePickerDialog

    /** Layout containing the list of "point of interest" tag filters*/
    private lateinit var linearLayoutTags: LinearLayout

    /** [RangeSlider] for price filtering. */
    private lateinit var rangeSliderPrice : RangeSlider

    /** [RangeSlider] for surface filtering. */
    private lateinit var rangeSliderSurface: RangeSlider

    /** [MaterialButton] for "Available estates" filtering */
    private lateinit var availableButton: MaterialButton

    /** [MaterialButton] for "Sold estates" filtering */
    private lateinit var soldButton: MaterialButton

    /** Defines status of both material buttons [availableButton] and [soldButton]*/
    private var availableStatus: Boolean = true

    /** Defines status of all [MaterialButton] contained in [linearLayoutTags] */
    private var listPOIStatus: MutableList<Boolean> = mutableListOf()

    /** [TextInputEditText] containing a start date for filtering */
    private lateinit var textInputStartDate: TextInputEditText

    /** [TextInputLayout] associated with [textInputStartDate] */
    private lateinit var textLayoutStartDate: TextInputLayout

    /** [TextInputEditText] containing an end date for filtering */
    private lateinit var textInputEndDate: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? { return inflater.inflate(R.layout.fragment_search, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateToolbarTitle()
        initializeSearchDialog()
        restoreDialog(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {  (activity as MainActivity).onBackPressed() }
            R.id.filter -> { builderSearchDialog.show() }
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
                                       .setPositiveButton(R.string.str_dialog_confirm) { _, _ -> }
                                       .setNegativeButton(R.string.str_dialog_button_cancel) { _, _ -> }
                                       .create()
            initializeSliders()
            initializeListPOISelectionInDialog()
            initializeDatePickerDialogs()
            initializeTextInputDate()
            handleCheckboxPrice()
            handleCheckBoxSurface()
            handleCheckBoxDate()
            handleCheckBoxStatus()
            handleCheckBoxPOIStatus()
            handleDateTextInputClicks()
            handleMaterialButtonsStatus()
            handleMaterialButtonsPOI()
        }
    }

    /**
     * Initializes [RangeSlider] views.
     */
    private fun initializeSliders() {
        viewLayoutDialog?.let {
            // Initialize "Price" slider
            val valuesPrice = resources.getIntArray(R.array.slider_price_values)
            val valuesSurface = resources.getIntArray(R.array.slider_surface_values)
            rangeSliderPrice = it.findViewById<RangeSlider>(R.id.range_slider_price).apply {
                this.setValues(valuesPrice[0].toFloat(), valuesPrice[1].toFloat())
                this.stepSize = valuesPrice[2].toFloat()
            }
            rangeSliderSurface = it.findViewById<RangeSlider>(R.id.range_slider_surface).apply {
                this.setValues(valuesSurface[0].toFloat(), valuesSurface[1].toFloat())
                this.stepSize = valuesSurface[2].toFloat()
            }
            // Set listeners
            handleRangeSliderPriceListener()
            handleRangeSliderSurfaceListener()
        }
    }

    /**
     * Initializes the list of points of interest available for filter selection.
     */
    private fun initializeListPOISelectionInDialog() {
        // Get list of points of interest from xml resource
        val list: Array<out String> = resources.getStringArray(R.array.poi)
        // Initialize layoutParams for views to add in HorizontalScrollView
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            marginStart = 20.toPx(activity as MainActivity)
        }
        context?.let { itContext ->
            // Add each button in HorizontalScrollView
            for (i in list.indices) {
                val materialButton = MaterialButton(itContext).apply {
                    text = list[i]
                    setColorsMaterialButton(R.color.grey41, R.color.grey93, this)
                    isAllCaps = false
                    isClickable = false
                }
                linearLayoutTags.addView(materialButton, layoutParams)
                // Update list status
                listPOIStatus.add(false)
            }
        }
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
            textInputStartDate.isEnabled = false
            textInputEndDate = it.findViewById(R.id.text_input_date_end)
            textInputEndDate.isEnabled = false
        }
    }

    /**
     * Restores displayed [builderSearchDialog] alert dialog after a configuration change
     * @param savedInstanceState : Bundle
     */
    private fun restoreDialog(savedInstanceState: Bundle?) {
        savedInstanceState?.let { builderSearchDialog.show() }
    }

    /**
     * Handles click events on "Price" [CheckBox].
     */
    private fun handleCheckboxPrice() {
        viewLayoutDialog?.let {
            it.findViewById<CheckBox>(R.id.checkbox_price).setOnCheckedChangeListener { _, isChecked ->
                rangeSliderPrice.isEnabled = isChecked
            }
        }
    }

    /**
     * Handles click events on "Surface" [CheckBox].
     */
    private fun handleCheckBoxSurface() {
        viewLayoutDialog?.let {
            it.findViewById<CheckBox>(R.id.checkbox_surface).setOnCheckedChangeListener { _, isChecked ->
                rangeSliderSurface.isEnabled = isChecked
            }
        }
    }

    /**
     * Handles click events on "Date" [CheckBox].
     */
    private fun handleCheckBoxDate() {
        viewLayoutDialog?.let {
            it.findViewById<CheckBox>(R.id.checkbox_date).setOnCheckedChangeListener { _, isChecked ->
                textInputStartDate.isEnabled = isChecked
                textInputEndDate.isEnabled = isChecked
            }
        }
    }

    /**
     * Handles click events on "Status" [CheckBox].
     */
    private fun handleCheckBoxStatus() {
        viewLayoutDialog?.let {
            availableButton = it.findViewById(R.id.button_available)
            soldButton = it.findViewById(R.id.button_sold)
            it.findViewById<CheckBox>(R.id.checkbox_status).setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (availableStatus) { // "Available" button selected
                        setColorsMaterialButton(R.color.colorPrimary, R.color.white, availableButton)
                        setColorsMaterialButton(R.color.white, R.color.colorPrimary, soldButton)
                    }
                    else { // "Sold button selected
                        setColorsMaterialButton(R.color.white, R.color.colorPrimary, availableButton)
                        setColorsMaterialButton(R.color.colorPrimary, R.color.white, soldButton)
                    }
                }
                else {
                    setColorsMaterialButton(R.color.grey41, R.color.grey93, availableButton)
                    setColorsMaterialButton(R.color.grey41, R.color.grey93, soldButton)
                }
                availableButton.isClickable = isChecked
                soldButton.isClickable = isChecked
            }
        }
    }

    /**
     * Handles click events on "Points of interest" [CheckBox].
     */
    private fun handleCheckBoxPOIStatus() {
        viewLayoutDialog?.let {
            it.findViewById<CheckBox>(R.id.checkbox_poi).setOnCheckedChangeListener {
                    _, isChecked ->
                for (i in 0 until linearLayoutTags.childCount) {
                    val materialButton = linearLayoutTags.getChildAt(i) as MaterialButton
                    if (isChecked) {
                        materialButton.isClickable = true
                        if (listPOIStatus[i])
                            setColorsMaterialButton(R.color.colorPrimary, R.color.white, materialButton)
                        else
                            setColorsMaterialButton(R.color.white, R.color.colorPrimary, materialButton)
                    }
                    else {
                        materialButton.isClickable = false
                        setColorsMaterialButton(R.color.grey41, R.color.grey93, materialButton)
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
                updateMaterialTextPrice(slider.values[0].toInt(), slider.values[1].toInt(), it)
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
                updateMaterialTextSurface(slider.values[0].toInt(), slider.values[1].toInt(), it)
            }
        }
    }

    /**
     * Handles click interactions with [textInputStartDate] and [textInputEndDate].
     */
    private fun handleDateTextInputClicks() {
        viewLayoutDialog?.let {
            textInputStartDate.setOnClickListener { builderStartDatePickerDialog.show() }
            textInputEndDate.setOnClickListener { builderEndDatePickerDialog.show() }
        }
    }

    /**
     * Handles click events on "available" and "sold" [MaterialButton]
     */
    private fun handleMaterialButtonsStatus() {
        viewLayoutDialog?.let {
            availableButton.setOnClickListener {
                availableStatus = true
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, availableButton)
                setColorsMaterialButton(R.color.white, R.color.colorPrimary, soldButton)
            }
            soldButton.setOnClickListener {
                availableStatus = false
                setColorsMaterialButton(R.color.white, R.color.colorPrimary, availableButton)
                setColorsMaterialButton(R.color.colorPrimary, R.color.white, soldButton)
            }
            availableButton.isClickable = false
            soldButton.isClickable = false
        }
    }

    /**
     * Handles click events on all "tag" [MaterialButton]
     */
    private fun handleMaterialButtonsPOI() {
        for (i in 0 until linearLayoutTags.childCount) {
            (linearLayoutTags.getChildAt(i) as MaterialButton).setOnClickListener {
                // Update status
                listPOIStatus[i] = !listPOIStatus[i]
                // Update button properties
                if (listPOIStatus[i]) setColorsMaterialButton(R.color.colorPrimary, R.color.white,
                    linearLayoutTags.getChildAt(i) as MaterialButton)
                else setColorsMaterialButton(R.color.white, R.color.colorPrimary,
                    linearLayoutTags.getChildAt(i) as MaterialButton)
            }
            (linearLayoutTags.getChildAt(i) as MaterialButton).isClickable = false
        }
    }

    /**
     * Updates min and max values displayed for [rangeSliderPrice].
     * @param minPrice : min value selected
     * @param maxPrice : max value selected
     * @param view : [viewLayoutDialog]
     */
    private fun updateMaterialTextPrice(minPrice: Int, maxPrice: Int, view: View) {
        view.findViewById<MaterialTextView>(R.id.min_price).apply {
            val text = resources.getString(R.string.str_value_price_slider, minPrice)
            setText(text)
        }
        view.findViewById<MaterialTextView>(R.id.max_price).apply {
            val text = resources.getString(R.string.str_value_price_slider, maxPrice)
            setText(text)
        }
    }

    /**
     * Updates min and max values displayed for [rangeSliderSurface].
     * @param minSurface : min value selected
     * @param maxSurface : max value selected
     * @param view : [viewLayoutDialog]
     */
    private fun updateMaterialTextSurface(minSurface: Int, maxSurface : Int, view: View) {
        view.findViewById<MaterialTextView>(R.id.min_surface).apply {
            val text = resources.getString(R.string.str_value_surface_slider, minSurface)//"$minSurface sqm"
            setText(text)
        }
        view.findViewById<MaterialTextView>(R.id.max_surface).apply {
            val text = resources.getString(R.string.str_value_surface_slider, maxSurface)//"$maxSurface sqm"
            setText(text)
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
        val currentDate = Utils.getDateFormat(selectedDate.time)
        if (type) textInputStartDate.text = StringHandler.convertStringToEditable(currentDate)
        else textInputEndDate.text = StringHandler.convertStringToEditable(currentDate)
    }

    /**
     * Checks [textLayoutStartDate] and [textInputEndDate] text values.
     */
    private fun checkTextInputEditTextFields() {
        if (textInputStartDate.text?.isNotEmpty() == true && textInputEndDate.text?.isNotEmpty() == true)
        {
            val status = DateComparator.compareDates(textInputStartDate.text.toString(),
                                                     textInputEndDate.text.toString())
            textLayoutStartDate.apply {
                isErrorEnabled = !status
                error = if (!status) "Error" else ""
            }
            builderSearchDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = status
        }
    }
    /**
     * Sets colors for text and background for a selected [MaterialButton]
     * @param textColor : color of the [MaterialButton] text
     * @param backgroundColor : color fo the [MaterialButton] background
     * @param button : [MaterialButton] to update
     */
    private fun setColorsMaterialButton(@ColorRes textColor: Int,
                                        @ColorRes backgroundColor: Int,
                                        button: MaterialButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            button.apply {
                setTextColor(resources.getColor(textColor, null))
                setBackgroundColor(resources.getColor(backgroundColor, null))
            }
        else
            button.apply {
                setTextColor(resources.getColor(textColor))
                setBackgroundColor(resources.getColor(backgroundColor))
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(AppInfo.DIALOG_SEARCH_KEY, builderSearchDialog.isShowing)
        }
    }
}