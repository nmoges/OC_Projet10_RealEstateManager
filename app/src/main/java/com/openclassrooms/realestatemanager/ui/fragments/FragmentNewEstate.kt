package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.data.model.Agent
import com.openclassrooms.data.model.Estate
import com.openclassrooms.data.model.Photo
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.ui.LayoutInflaterProvider
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.CustomTextWatcher
import com.openclassrooms.realestatemanager.utils.MediaAccessHandler
import com.openclassrooms.realestatemanager.utils.StringHandler
import com.openclassrooms.realestatemanager.utils.poi.POIProvider
import com.openclassrooms.realestatemanager.viewmodels.DialogsViewModel
import com.openclassrooms.realestatemanager.viewmodels.EstateViewModel
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [Fragment] subclass used to display a view allowing user to create a new real estate.
 */
class FragmentNewEstate : Fragment() {
    companion object { fun newInstance(): FragmentNewEstate = FragmentNewEstate() }

    /** View Binding parameter */
    private lateinit var binding: FragmentNewEstateBinding

    /** Contains a reference to a [ListEstatesViewModel]  */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains a reference to a [DialogsViewModel] */
    private lateinit var dialogsViewModel: DialogsViewModel

    /** Contains a reference to a [EstateViewModel] */
    private lateinit var estateViewModel: EstateViewModel

    /** Defines new estate creation (false) or to modification of an existing one (true). */
     var updateEstate: Boolean = false

    /** Defines [AlertDialog] for "Add point of interest" functionality */
    private var builderAddPOIDialog: AlertDialog? = null

    /** Defines [AlertDialog] for list of agents access */
    private var builderListAgentsDialog: AlertDialog? = null

    /** Defines an [AlertDialog] allowing user to reset [Estate] information. */
    private var builderResetEstateDialog: AlertDialog? = null

    /** Defines an [AlertDialog] allowing user to add a new photo. */
    private var builderAddMediaDialog: AlertDialog? = null

    /** Defines an [AlertDialog] allowing user to cancel creation or modification of an [Estate]. */
    var builderCancelEstateDialog: AlertDialog? = null

    /** Defines an [AlertDialog] allowing user to confirm photo addition. */
    private var builderNameMediaDialog: AlertDialog? = null

    /** Contains text value of an [AlertDialog] */
    private var textNameMediaDialog: String = ""

    /** Defines an [AlertDialog] displaying a circular progress bar */
    private var builderProgressBarDialog: AlertDialog? = null

    /** Defines if fragment must be removed or if a confirmation dialog must be showed after an
     * onBackPressed event */
    var confirmExit: Boolean = false

    /** Defines a [TextWatcher] for [builderNameMediaDialog] */
    private val textWatcher: TextWatcher = object : CustomTextWatcher() {
        override fun afterTextChanged(sequence: Editable?) {
            if (sequence != null) {
                textNameMediaDialog = sequence.toString()
                builderNameMediaDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
                                                                             sequence.isNotEmpty() }
        }
    }

    /** Contains status error sliders */
    private var errorSliders = false

    /** Contains saved currency unit */
    private lateinit var currency: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
        estateViewModel = ViewModelProvider(requireActivity())[EstateViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentNewEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateEstate = estateViewModel.typeOperation
        updatePriceUnitDisplayed()
        initializeListPOI()
        updateTagsDisplay()
        updatePOIButtonTextDisplay()
        initializeDialogs()
        updateToolbarTitle()
        updateMaterialButtonText()
        updateHorizontalScrollViewWithPhotos()
        handleAddPhotoButton()
        initializeSlidersValues()
        initializeSliderMaterialText()
        handleSlidersListeners()
        handleConfirmationButtonListener()
        handleEditTextWatchers()
        handleNameAgentEditListener()
        editLocation()
        handleAddPOIButton()
        checkDialogStatusInViewModel()
        updateFragmentViewsWithEstateProperties()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_new_estate, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                dialogsViewModel.cancelEstateDialogStatus = true
                builderCancelEstateDialog?.show() }
            R.id.reset -> {
                dialogsViewModel.resetEstateDialogStatus = true
                builderResetEstateDialog?.show() }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Initializes all dialogs.
     */
    private fun initializeDialogs() {
        initializeDialogResetEstate()
        initializeDialogCancelEstate()
        initializeDialogAddMedia()
        initializeDialogNameMedia()
        initializeDialogListAgents()
        initializeDialogAddPOI()
        initializeDialogProgressBar()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderResetEstateDialog] property.
     */
    private fun initializeDialogResetEstate() {
        builderResetEstateDialog =  AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_reset_title))
            .setMessage(resources.getString(R.string.str_dialog_reset_message))
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) { _, _ -> confirmReset() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_no)) { _, _ -> }
            .create()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderListAgentsDialog] property.
     */
    private fun initializeDialogListAgents() {
        listEstatesViewModel.listAgents.observe(viewLifecycleOwner, { itList ->
            val listAgents = mutableListOf<Agent>()
            itList.forEach { itAgent ->
                listAgents.add(itAgent)
            }
            val context = context
            if (context != null) {
                val adapter: ArrayAdapter<String> = ArrayAdapter(context,android.R.layout.select_dialog_item)
                if (listAgents != null) {
                    listAgents.forEach { adapter.add("${it.firstName} ${it.lastName}") }
                    builderListAgentsDialog = AlertDialog.Builder(activity)
                        .setTitle(resources.getString(R.string.str_dialog_select_agent_title))
                        .setAdapter(adapter) { _, which ->
                            updateNameAgentEditText(which+1, adapter.getItem(which)) }.create() }
            }
        })
        listEstatesViewModel.restoreListAgents()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderAddPOIDialog] property.
     */
    private fun initializeDialogAddPOI() {
        val itemsBoolean = BooleanArray(8)
        estateViewModel.listPOI.forEach {
            val index = POIProvider.provideIndexFromPointOfInterest(it)
            itemsBoolean[index] = true
        }
        builderAddPOIDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_ad_poi_title))
            .setMultiChoiceItems(R.array.poi, itemsBoolean) { _, which, isChecked ->
                val pointOfInterest = POIProvider.providePointOfInterest(which)
                if (isChecked)
                    estateViewModel.listPOI.add(pointOfInterest)
                else
                    if (estateViewModel.listPOI.contains(pointOfInterest))
                        estateViewModel.listPOI.remove(pointOfInterest)
                updateTagsDisplay()
                updatePOIButtonTextDisplay()
            }
            .setPositiveButton(resources.getString(R.string.str_dialog_button_close)) { _, _ -> }
            .create()
    }

    private fun initializeDialogProgressBar() {
        val viewProgressBarDialog: View? = LayoutInflaterProvider
            .getViewFromLayoutInflater(R.layout.dialog_progress_bar, context)
        val message = viewProgressBarDialog?.findViewById<MaterialTextView>(R.id.message)
        message?.text = if (updateEstate)
                                   resources.getString(R.string.str_dialog_progress_bar_update_text)
                       else resources.getString(R.string.str_dialog_progress_bar_creation_text)
        builderProgressBarDialog = AlertDialog.Builder(activity)
                                              .setView(viewProgressBarDialog)
                                              .create()
    }
    /**
     * Initializes an [AlertDialog.Builder] for [builderCancelEstateDialog] property.
     */
    private fun initializeDialogCancelEstate() {
          val title = if (updateEstate) resources.getString(R.string.str_dialog_cancel_modification_title)
        else resources.getString(R.string.str_dialog_cancel_creation_title)
        val message = if (updateEstate) resources.getString(R.string.str_dialog_cancel_modifications_message)
        else resources.getString(R.string.str_dialog_cancel_creation_message)
        builderCancelEstateDialog = AlertDialog.Builder(activity)
            .setTitle(title).setMessage(message)
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) {_, _ ->
                builderCancelEstateDialog?.dismiss()
                estateViewModel.removePhotosIfEstateCreationCancelled()
                estateViewModel.resetEstate()
                confirmExit = true
                (activity as MainActivity).onBackPressed() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_no)) {_, _ -> }.create()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderAddMediaDialog] property.
     */
    private fun initializeDialogAddMedia() {
        val viewAddMediaDialog: View? = LayoutInflaterProvider
                                .getViewFromLayoutInflater(R.layout.dialog_media_selection, context)
        builderAddMediaDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_add_media_title))
            .setView(viewAddMediaDialog).create()
        handleAddMediaDialogButtons(viewAddMediaDialog)
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderNameMediaDialog] property.
     */
    private fun initializeDialogNameMedia() {
        val viewNameMediaDialog: View? = LayoutInflaterProvider
                             .getViewFromLayoutInflater(R.layout.dialog_media_confirmation, context)
        val textInputEditText: TextInputEditText? =
            viewNameMediaDialog?.findViewById(R.id.new_media_text_input_edit)
        textInputEditText?.addTextChangedListener(textWatcher)
        textInputEditText?.text = StringHandler.convertStringToEditable(dialogsViewModel.textNameMediaDialog)
        builderNameMediaDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_name_media_title))
            .setView(viewNameMediaDialog)
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) {_, _ -> addNewPhoto()
                textInputEditText?.text?.clear() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) {_, _ ->
                textInputEditText?.text?.clear() }
            .create()
    }

    /**
     * Initializes the sliders values associated with the current estate.
     */
    private fun initializeSlidersValues() {
        binding.apply {
            estateViewModel.let {
                this.sliderSurface.value = it.estate.interior.surface.toFloat()
                this.sliderRooms.value = it.estate.interior.numberRooms.toFloat()
                this.sliderBathrooms.value = it.estate.interior.numberBathrooms.toFloat()
                this.sliderBedrooms.value = it.estate.interior.numberBedrooms.toFloat()
            }
            checkSlidersValues()
        }
    }

    /**
     * Initializes the sliders associated texts.
     */
    private fun initializeSliderMaterialText() {
        binding.apply {
            sliderSurfaceValue.text = StringHandler.getSliderString(1000,
                           sliderSurface.value.toInt(), R.string.str_sqm_unit_greater_than_or_equal,
                           R.string.str_sqm_unit, true, context)
            sliderRoomsValue.text = StringHandler.getSliderString(20,
                           sliderRooms.value.toInt(), R.string.str_greater_than_or_equal,
                   null, false, context)
            sliderBathroomsValue.text = StringHandler.getSliderString(5,
                           sliderBathrooms.value.toInt(),
                           R.string.str_greater_than_or_equal, null, false, context)
            sliderBedroomsValue.text = StringHandler.getSliderString(10,
                           sliderBedrooms.value.toInt(),
                           R.string.str_greater_than_or_equal, null, false, context) }
    }

    /**
     * Adds a selected photo from gallery of from camera to the list and display it.
     */
    private fun addNewPhoto() {
        val newPhoto: Photo?  = estateViewModel.createNewPhoto(textNameMediaDialog)
        newPhoto?.let {
            estateViewModel.estate.listPhoto.add(it)
            estateViewModel.numberPhotosAdded++
            addNewFrameLayoutToBinding(estateViewModel.estate.listPhoto.last())
        }

    }

    /**
     * Adds a new [Photo] to a frame layout to display.
     * @param photo : new photo
     */
    private fun addNewFrameLayoutToBinding(photo: Photo) {
        val frameLayout: FrameLayout = MediaDisplayHandler
            .createNewFrameLayout(photo, activity as MainActivity)
        binding.linearLayoutMedia.addView(frameLayout, 0)
    }

    /**
     * Handles click interactions with option from "Add media" dialog
     * @param view : dialog
     */
    private fun handleAddMediaDialogButtons(view: View?) {
        // Item "Take picture"
        view?.findViewById<ConstraintLayout>(R.id.take_picture_button)?.setOnClickListener {
            if (MediaAccessHandler.checkPermissions(activity as MainActivity))
                MediaAccessHandler.openCamera(activity as MainActivity)
            else MediaAccessHandler.requestPermission(activity as MainActivity)
                builderAddMediaDialog?.dismiss() }
        // Item "Import from gallery"
        view?.findViewById<ConstraintLayout>(R.id.import_from_gallery_button)?.setOnClickListener {
            if (MediaAccessHandler.checkPermissions(activity as MainActivity))
                MediaAccessHandler.openPhotosGallery(activity as MainActivity)
            else MediaAccessHandler.requestPermission(activity as MainActivity)
                builderAddMediaDialog?.dismiss() }
    }

    /**
     * Initializes TextInputEdit fields with current [Estate] properties values to modify.
     */
    private fun updateFragmentViewsWithEstateProperties() {
        with(binding) {
            // TextInputEditText
            nameSectionEdit.text = StringHandler.convertStringToEditable(estateViewModel.estate.type)
            locationSectionEdit.text = StringHandler.convertStringToEditable(estateViewModel.estate.location.address)
            descSectionEdit.text = StringHandler.convertStringToEditable(estateViewModel.estate.description)
            val nameAgent = "${estateViewModel.estate.agent.firstName} ${estateViewModel.estate.agent.lastName}"
            agentSectionEdit.text = StringHandler.convertStringToEditable(nameAgent)
            if (currency == "EUR") {
                val priceConverted = Utils.convertDollarToEuro(estateViewModel.estate.price)
                priceSectionEdit.text = StringHandler.convertStringToEditable(priceConverted.toString())
            }
            else priceSectionEdit.text =
                               StringHandler.convertStringToEditable(estateViewModel.estate.price.toString())
            // Sliders
            sliderSurface.value = estateViewModel.estate.interior.surface.toFloat()
            sliderRooms.value = estateViewModel.estate.interior.numberRooms.toFloat()
            sliderBathrooms.value = estateViewModel.estate.interior.numberBathrooms.toFloat()
            sliderBedrooms.value = estateViewModel.estate.interior.numberBedrooms.toFloat()
        }
    }

    /**
     * Updates MaterialButton text according to [updateEstate] value.
     */
    private fun updateMaterialButtonText() {
        binding.confirmationButton.apply {
            text = if (updateEstate) resources.getString(R.string.str_button_confirmation_modification)
                   else resources.getString(R.string.str_button_confirmation_creation)
        }
    }

    /**
     * Updates [MainActivity] toolbar title according to [updateEstate] value.
     */
    private fun updateToolbarTitle() {
        (activity as MainActivity).apply {
            if (updateEstate) setToolbarProperties(R.string.str_toolbar_fragment_modify_estate_title, true)
            else setToolbarProperties(R.string.str_toolbar_fragment_new_estate_title, true)
        }
    }

    /**
     * Clears all views in fragment.
     */
    private fun confirmReset() {
        with(binding) {
            nameSectionEdit.text?.clear()
            locationSectionEdit.text?.clear()
            descSectionEdit.text?.clear()
            agentSectionEdit.text?.clear()
            priceSectionEdit.text?.clear()
            sliderSurface.value = 50.0F
            sliderRooms.value = 5.0F
            sliderBathrooms.value = 1.0F
            sliderBedrooms.value = 1.0F
            linearLayoutMedia.removeViews(0, estateViewModel.estate.listPhoto.size)
            tagContainerLayout.removeAllTags()
            updatePOIButtonTextDisplay()}
    }

    /**
     * Handles "Add photo" button.
     */
    private fun handleAddPhotoButton() {
        binding.buttonAddPhoto.setOnClickListener {
            builderAddMediaDialog?.isShowing?.let {
                if (!it) {
                    dialogsViewModel.addMediaDialogStatus = true
                    builderAddMediaDialog?.show()
                }
            }
        }
    }

    /**
     * Handles clicks event on location edit text field.
     */
    private fun editLocation() {
        binding.locationSectionEdit.setOnClickListener {
            listEstatesViewModel.performAutocompleteRequest(activity as MainActivity) }
    }

    /**
     * Updates location estate displayed in the "Location" edit text view.
     */
    fun updateLocationDisplayed() {
        binding.locationSectionEdit.text =
                      StringHandler.convertStringToEditable(estateViewModel.estate.location.address)
    }

    /**
     * Gets a [Uri] photo value from Gallery or Camera.
     */
    fun addNewPhotoUri(uri: Uri?) {
        if (uri != null) {
            estateViewModel.photoUriEstate = uri.toString()
            displayNameMediaDialog()
        }
    }

    /**
     * Displays an [AlertDialog] for name photo entry.
     */
    private fun displayNameMediaDialog() {
        builderNameMediaDialog.apply {
            dialogsViewModel.nameMediaDialogStatus = true
            this?.show()
            this?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        }
    }

    /**
     * Restores the list of photo of an [Estate].
     */
    private fun updateHorizontalScrollViewWithPhotos() {
        if (estateViewModel.estate != null) {
            estateViewModel.estate.listPhoto.forEach { photo -> addNewFrameLayoutToBinding(photo) }
        }
    }

    /**
     * Handles user interactions with sliders
     */
    private fun handleSlidersListeners() {
        var text: String?
        binding.apply {
            sliderSurface.addOnChangeListener{ _, _, _ ->
                text = StringHandler.getSliderString(1000, sliderSurface.value.toInt(),
                R.string.str_sqm_unit_greater_than_or_equal, R.string.str_sqm_unit, true, context)
                sliderSurfaceValue.text = text }
            sliderRooms.addOnChangeListener{ _, _, _ ->
                checkSlidersValues()
                text = StringHandler.getSliderString(20, sliderRooms.value.toInt(),
                R.string.str_greater_than_or_equal, null, false, context)
                sliderRoomsValue.text = text }
            sliderBathrooms.addOnChangeListener{ _, _, _ ->
                checkSlidersValues()
                text = StringHandler.getSliderString(5, sliderBathrooms.value.toInt(),
                R.string.str_greater_than_or_equal, null, false, context)
                sliderBathroomsValue.text = text }
            sliderBedrooms.addOnChangeListener{ _, _, _ ->
                checkSlidersValues()
                text = StringHandler.getSliderString(10, sliderBedrooms.value.toInt(),
                R.string.str_greater_than_or_equal, null, false, context)
                sliderBedroomsValue.text = text }
        }
    }

    /**
     * Checks and compares sliders values, to update sliders display.
     */
    private fun checkSlidersValues() {
        errorSliders =
            binding.sliderBedrooms.value + binding.sliderBathrooms.value > binding.sliderRooms.value
        if (errorSliders) updateSlidersColor(R.color.red_google, R.color.red_google_light)
        else updateSlidersColor(R.color.colorPrimary, R.color.colorPrimaryLight)
    }

    /**
     * Updates sliders with selected colors
     * @param color : color to apply
     * @param colorLight : light color to apply
     */
    private fun updateSlidersColor(@ColorRes color: Int, @ColorRes colorLight: Int) {
        binding.apply {
            sliderRooms.apply {
                tickTintList = ColorStateList.valueOf(resources.getColor(color))
                thumbTintList = ColorStateList.valueOf(resources.getColor(color))
                trackActiveTintList = ColorStateList.valueOf(resources.getColor(color))
                trackInactiveTintList = ColorStateList.valueOf(resources.getColor(colorLight))
            }
            sliderBathrooms.apply {
                tickTintList = ColorStateList.valueOf(resources.getColor(color))
                thumbTintList = ColorStateList.valueOf(resources.getColor(color))
                trackActiveTintList = ColorStateList.valueOf(resources.getColor(color))
                trackInactiveTintList = ColorStateList.valueOf(resources.getColor(colorLight))
            }
            sliderBedrooms.apply {
                tickTintList = ColorStateList.valueOf(resources.getColor(color))
                thumbTintList = ColorStateList.valueOf(resources.getColor(color))
                trackActiveTintList = ColorStateList.valueOf(resources.getColor(color))
                trackInactiveTintList = ColorStateList.valueOf(resources.getColor(colorLight))
            }
        }
    }

    /**
     * Handle user interactions with confirmation button.
     */
    private fun handleConfirmationButtonListener() {
        binding.apply {
            confirmationButton.setOnClickListener {
                val name: String = nameSectionEdit.text.toString()
                val location: String = locationSectionEdit.text.toString()
                val description: String = descSectionEdit.text.toString()
                var price: String = priceSectionEdit.text.toString()
                val nameAgent: String = agentSectionEdit.text.toString()
                if (name.isNotEmpty() && location.isNotEmpty() && nameAgent.isNotEmpty()
                    && description.isNotEmpty() && price.isNotEmpty()
                    && estateViewModel.estate.listPhoto.isNotEmpty()
                    && !errorSliders
                    && Utils.isInternetAvailable(context)) {
                    if (currency == "EUR") price = Utils.convertEuroToDollar(price.toInt()).toString()
                    updateSelectedEstateFromViewModel()
                    builderProgressBarDialog?.show()
                }
                else {
                    displayToastError()
                    if (Utils.isInternetAvailable(context)) displayErrorBoxMessage()
                }
            }
        }
    }

    /**
     * Displays [Toast] error message.
     */
    private fun displayToastError() {
        if (Utils.isInternetAvailable(context)) // Error creating/modifying Estate
            Toast.makeText(context, resources.getString(R.string.str_toast_missing_information),
                Toast.LENGTH_LONG).show()
        else {
            Toast.makeText(context, resources.getString(R.string.str_toast_no_network),
                Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Displays [Toast] success message.
     */
    private fun displayToastSuccess() {
        // Modifications saved
        if (updateEstate) Toast.makeText(context, resources.getString(R.string.str_toast_estate_modified), Toast.LENGTH_LONG).show()
        // New estate created
        else Toast.makeText(context, resources.getString(R.string.str_toast_new_estate_created), Toast.LENGTH_LONG).show()
    }
    /**
     * Update selected estate from view model which is used to store database modifications.
     */
    private fun updateSelectedEstateFromViewModel() {
        saveEstateValuesInViewModel()
        estateViewModel.getNewEstate((activity as MainActivity).getFirebaseAuth()).observe(viewLifecycleOwner,
            { itEstate ->
                estateViewModel.updateSQLiteDatabase(updateEstate, itEstate,
                    (activity as MainActivity).getFirebaseDatabaseReference()) {
                    // Callback after database updated
                    confirmExit = true
                    builderProgressBarDialog?.dismiss()
                    displayToastSuccess()
                    (activity as MainActivity).apply {
                        notificationHandler.createNotification(updateEstate)
                        onBackPressed()
                }
            }
        })
    }

    /**
     * Handles display of error message of TextInputLayouts.
     */
    private fun displayErrorBoxMessage() {
        binding.apply {
            if (nameSectionEdit.text?.isEmpty() == true)
                updateErrorStatus(true, nameSectionLayout)
            if (locationSectionEdit.text?.isEmpty() == true)
                updateErrorStatus(true, locationSectionLayout)
            if (descSectionEdit.text?.isEmpty() == true)
                updateErrorStatus(true, descSectionLayout)
            if (priceSectionEdit.text?.isEmpty() == true)
                updateErrorStatus(true, priceSectionLayout)
            if (agentSectionEdit.text?.isEmpty() == true)
                updateErrorStatus(true, agentSectionLayout)
        }
    }

    /**
     * Handles TextInputEditText views text watchers.
     */
    private fun handleEditTextWatchers() {
        binding.apply {
            nameSectionEdit.addTextChangedListener(object : CustomTextWatcher() {
                override fun afterTextChanged(sequence: Editable?) {
                    updateErrorStatus(false, nameSectionLayout)} })
            descSectionEdit.addTextChangedListener(object : CustomTextWatcher() {
                override fun afterTextChanged(sequence: Editable?) {
                    updateErrorStatus(false, descSectionLayout)} })
            priceSectionEdit.addTextChangedListener(object : CustomTextWatcher() {
                override fun afterTextChanged(sequence: Editable?) {
                    updateErrorStatus(false, priceSectionLayout)} })
        }
    }

    /**
     * Updates error status display in associated [TextInputLayout].
     */
    private fun updateErrorStatus(status: Boolean, layout: TextInputLayout) {
        if (status) layout.apply {
                                    isErrorEnabled = true
                                    error = "Empty" }
        else layout.isErrorEnabled = false
    }

    /**
     * Handle user interactions with TextInputEditText field for agent selection.
     */
    private fun handleNameAgentEditListener() {
        binding.agentSectionEdit.setOnClickListener {
            dialogsViewModel.listAgentsDialogStatus = true
            builderListAgentsDialog?.show()
        }
    }

    /**
     * Updates TextInputEditText field for agent selection.
     */
    private fun updateNameAgentEditText(position: Int, nameAgent: String?) {
        estateViewModel.idAgentSelected = position.toLong()
        binding.agentSectionEdit.text = Editable.Factory.getInstance().newEditable(nameAgent)
    }

    /**
     * Handles click events on "Points of interest" button.
     */
    private fun handleAddPOIButton() {
        binding.constraintLayoutAddPoi.setOnClickListener {
            dialogsViewModel.addPOIDialogStatus = true
            builderAddPOIDialog?.show()
        }
    }

    /**
     * Updates visibility status of POI button text.
     */
    private fun updatePOIButtonTextDisplay() {
            binding.textPlacesNearby.visibility =
                            if (binding.tagContainerLayout.tags.size > 0) View.INVISIBLE
                            else View.VISIBLE
    }

    /**
     * Update "Point of interest" tags displayed in container.
     */
    private fun updateTagsDisplay() {
        binding.tagContainerLayout.let {  itContainer ->
            itContainer.removeAllTags()
            estateViewModel.listPOI.forEach { itPOI -> itContainer.addTag(itPOI) }
        }
    }

    /**
     * Initializes the list of POI used for container updates.
     */
    private fun initializeListPOI() {
        binding.tagContainerLayout.let { itContainer ->
            itContainer.removeAllTags()
            estateViewModel.listPOI.forEach { itPoi -> itContainer.addTag(itPoi) }
        }
    }

    private fun updatePriceUnitDisplayed() {
        context?.getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)?.apply {
            this.getString(AppInfo.PREF_CURRENCY, "USD")?.let {
                currency = it
                when (currency) {
                    "USD" -> { binding.newEstatePriceSectionUnit.text =
                          resources.getString(R.string.str_new_estate_surface_section_price_dollar) }
                    "EUR" -> { binding.newEstatePriceSectionUnit.text =
                        resources.getString(R.string.str_new_estate_surface_section_price_euro) }
                }
            }
        }
    }

    /**
     * Dismiss displayed dialog before configuration change.
     */
    private fun dismissDialogDisplayed() {
        builderListAgentsDialog?.let { if (it.isShowing) it.dismiss() }
        builderAddPOIDialog?.let { if (it.isShowing) it.dismiss() }
        builderCancelEstateDialog?.let { if (it.isShowing) it.dismiss() }
        builderNameMediaDialog?.let { if (it.isShowing) it.dismiss() }
        builderAddMediaDialog?.let { if (it.isShowing) it.dismiss() }
        builderResetEstateDialog?.let { if (it.isShowing) it.dismiss() }
        builderProgressBarDialog?.let { if (it.isShowing) it.dismiss() }
    }

    override fun onPause() {
        super.onPause()
        saveDialogsStatusInViewModel()
        saveEstateValuesInViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDialogDisplayed()
    }

    /**
     * Checks in [DialogsViewModel] if dialogs status before configuration change.
     */
    private fun checkDialogStatusInViewModel() {
        if (dialogsViewModel.listAgentsDialogStatus) builderAddMediaDialog?.show()
        if (dialogsViewModel.addPOIDialogStatus) builderAddPOIDialog?.show()
        if (dialogsViewModel.cancelEstateDialogStatus) builderCancelEstateDialog?.show()
        if (dialogsViewModel.nameMediaDialogStatus) builderNameMediaDialog?.show()
        if (dialogsViewModel.addMediaDialogStatus) builderAddMediaDialog?.show()
        if (dialogsViewModel.resetEstateDialogStatus) builderResetEstateDialog?.show()
        if (dialogsViewModel.progressBarDialogStatus) builderProgressBarDialog?.show()
    }

    /**
     * Saves dialog status values in view model.
     */
    private fun saveDialogsStatusInViewModel() {
        builderListAgentsDialog?.let { dialogsViewModel.listAgentsDialogStatus = it.isShowing }
        builderAddPOIDialog?.let { dialogsViewModel.addPOIDialogStatus = it.isShowing }
        builderCancelEstateDialog?.let { dialogsViewModel.cancelEstateDialogStatus = it.isShowing }
        builderNameMediaDialog?.let { dialogsViewModel.nameMediaDialogStatus = it.isShowing }
        builderAddMediaDialog?.let { dialogsViewModel.addMediaDialogStatus = it.isShowing }
        builderResetEstateDialog?.let { dialogsViewModel.resetEstateDialogStatus = it.isShowing }
        dialogsViewModel.textNameMediaDialog = textNameMediaDialog
        builderProgressBarDialog?.let { dialogsViewModel.progressBarDialogStatus = it.isShowing }
    }

    /**
     * Saves view values in view model.
     */
    private fun saveEstateValuesInViewModel() {
        binding.let {
            estateViewModel.apply {
                this.estate.type = it.nameSectionEdit.text.toString()
                if (it.priceSectionEdit.text?.isNotEmpty() == true)
                            this.estate.price = it.priceSectionEdit.text.toString().toInt()
                this.estate.location.address = it.locationSectionEdit.text.toString()
                this.estate.description = it.descSectionEdit.text.toString()
                this.estate.interior.surface = binding.sliderSurface.value.toInt()
                this.estate.interior.numberBathrooms = binding.sliderBathrooms.value.toInt()
                this.estate.interior.numberBedrooms = binding.sliderBedrooms.value.toInt()
                this.estate.interior.numberRooms = binding.sliderRooms.value.toInt()
                this.typeOperation = updateEstate
                this.errorSlidersStatus = errorSliders
                this.nameAgentSelected = binding.agentSectionEdit.text.toString()
            }
        }
    }
}