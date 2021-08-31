package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Photo
import com.openclassrooms.realestatemanager.ui.LayoutInflaterProvider
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.CustomTextWatcher
import com.openclassrooms.realestatemanager.utils.MediaAccessHandler
import com.openclassrooms.realestatemanager.utils.StringHandler
import com.openclassrooms.realestatemanager.utils.poi.POIProvider
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [Fragment] subclass used to display a view allowing user to create a new real estate.
 */
class FragmentNewEstate : Fragment() {
    companion object { fun newInstance(): FragmentNewEstate = FragmentNewEstate() }

    /** View Binding parameter */
    private lateinit var binding: FragmentNewEstateBinding

    /** Contains ViewModel reference */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Defines new estate creation (false) or to modification of an existing one (true). */
    var updateEstate: Boolean = false

    /** Defines [AlertDialog] for "Add point of interest" functionality */
    private lateinit var builderAddPOIDialog: AlertDialog

    /** Defines [AlertDialog] for list of agents access */
    private lateinit var builderListAgentsDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to reset [Estate] information. */
    private lateinit var builderResetEstateDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to add a new photo. */
    private lateinit var builderAddMediaDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to cancel creation or modification of an [Estate]. */
    lateinit var builderCancelEstateDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to confirm photo addition. */
    private lateinit var builderNameMediaDialog: AlertDialog

    /** Contains text value of an [AlertDialog] */
    private var textNameMediaDialog: String = ""

    /** Contains the number of added photos during estate creation/update */
    private var numberPhotosAdded = 0

    /** Defines if fragment must be removed or if a confirmation dialog must be showed after an
     * onBackPressed event */
    var confirmExit: Boolean = false

    /** Defines a [TextWatcher] for [builderNameMediaDialog] */
    private val textWatcher: TextWatcher = object : CustomTextWatcher() {
        override fun afterTextChanged(sequence: Editable?) {
            if (sequence != null) {
                textNameMediaDialog = sequence.toString()
                builderNameMediaDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .isEnabled = sequence.isNotEmpty() }
        }
    }

    /** Contains temporary value of a selected agent */
    private var agentSelected = 1

    /** Contains temporary values of selected points of interest */
    private var listPOI = mutableListOf<String>()

    /** Contains status error sliders */
    private var errorSliders = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentNewEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeListPOI()
        updateTagsDisplay()
        initializeDialogs()
        if (savedInstanceState != null) {
            confirmExit = savedInstanceState.getBoolean(AppInfo.CONFIRM_EXIT_KEY, false)
            numberPhotosAdded = savedInstanceState.getInt(AppInfo.NUMBER_PHOTO_KEY, 0)
            textNameMediaDialog = savedInstanceState.getString(AppInfo.TEXT_DIALOG_CONFIRM_MEDIA_KEY, "")
            restoreDialogs(savedInstanceState)
            updateEstate = savedInstanceState.getBoolean(AppInfo.UPDATE_ESTATE_KEY, false)
            agentSelected = savedInstanceState.getInt(AppInfo.ESTATE_AGENT_KEY, 1)
            errorSliders = savedInstanceState.getBoolean(AppInfo.ERROR_SLIDERS_KEY)
        }
        updateToolbarTitle()
        updateMaterialButtonText()
        if (updateEstate) updateFragmentViewsWithEstateProperties()
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_new_estate, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> { builderCancelEstateDialog.show() }
            R.id.reset -> { builderResetEstateDialog.show() }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Restores displayed dialog after a configuration change.
     * @param savedInstanceState : Bundle
     */
    private fun restoreDialogs(savedInstanceState: Bundle?) {
        when {
            savedInstanceState?.getBoolean(AppInfo.DIALOG_CANCEL_KEY) == true -> {
                builderCancelEstateDialog.show() }
            savedInstanceState?.getBoolean(AppInfo.DIALOG_RESET_KEY) == true -> {
                builderResetEstateDialog.show() }
            savedInstanceState?.getBoolean(AppInfo.DIALOG_ADD_MEDIA_KEY) == true -> {
                builderAddMediaDialog.show() }
            savedInstanceState?.getBoolean(AppInfo.DIALOG_CONFIRM_MEDIA_KEY) == true -> {
                builderNameMediaDialog.show()
                restoreBuildNameMediaDialogProperties() }
            savedInstanceState?.getBoolean(AppInfo.DIALOG_LIST_AGENTS_KEY) == true -> {
                builderListAgentsDialog.show() }
            savedInstanceState?.getBoolean(AppInfo.DIALOG_ADD_POI_KEY) == true -> {
                builderAddPOIDialog.show()
            }
        }
    }

    /**
     * Handles edit text and button status restoration after configuration change.
     */
    private fun restoreBuildNameMediaDialogProperties() {
        builderNameMediaDialog.apply {
            val editText = this.findViewById<TextInputEditText>(R.id.new_media_text_input_edit)
            editText.text = Editable.Factory.getInstance().newEditable(textNameMediaDialog)
            getButton(AlertDialog.BUTTON_POSITIVE)
                .isEnabled = textNameMediaDialog.isNotEmpty() }
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
        val context = context
        if (context != null) {
            val adapter: ArrayAdapter<String> = ArrayAdapter(context,android.R.layout.select_dialog_item)
            val list = listEstatesViewModel.listAgents.value
            if (list != null) {
                list.forEach { adapter.add("${it.firstName} ${it.lastName}") }
                builderListAgentsDialog = AlertDialog.Builder(activity)
                    .setTitle(resources.getString(R.string.str_dialog_select_agent_title))
                    .setAdapter(adapter) { _, which ->
                        updateNameAgentEditText(which+1, adapter.getItem(which)) }.create() }
        }
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderAddPOIDialog] property.
     */
    private fun initializeDialogAddPOI() {
        val itemsBoolean = BooleanArray(8)
        listPOI.forEach {
            val index = POIProvider.provideIndexFromPointOfInterest(it)
            itemsBoolean[index] = true
        }
        builderAddPOIDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_ad_poi_title))
            .setMultiChoiceItems(R.array.poi, itemsBoolean) { _, which, isChecked ->
                val pointOfInterest = POIProvider.providePointOfInterest(which)
                if (isChecked) listPOI.add(pointOfInterest)
                else { if (listPOI.contains(pointOfInterest)) listPOI.remove(pointOfInterest) }
                listEstatesViewModel.updatePointOfInterestSelectedEstate(listPOI)
                updateTagsDisplay()
                updatePOIButtonTextDisplay()
            }
            .setPositiveButton(resources.getString(R.string.str_dialog_button_close)) { _, _ -> }
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
                builderCancelEstateDialog.dismiss()
                listEstatesViewModel.removePhotosIfEstateCreationCancelled(numberPhotosAdded)
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
            val interior = listEstatesViewModel.selectedEstate?.interior
            if (interior != null) {
                sliderSurface.value = interior.surface.toFloat()
                sliderRooms.value = interior.numberRooms.toFloat()
                sliderBathrooms.value = interior.numberBathrooms.toFloat()
                sliderBedrooms.value = interior.numberBedrooms.toFloat() }
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
                           R.string.str_greater_than_or_equal, null, false, context)
        }
    }

    /**
     * Adds a selected photo from gallery of from camera to the list and display it.
     */
    private fun addNewPhoto() {
        listEstatesViewModel.selectedEstate?.run {
            val newPhoto: Photo? = listEstatesViewModel.createNewPhoto(textNameMediaDialog)
            listEstatesViewModel.clearTempPhotoUri()
            if (newPhoto != null) {
                listPhoto.add(newPhoto)
                numberPhotosAdded++
                addNewFrameLayoutToBinding(listPhoto.last())
            }
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
                builderAddMediaDialog.dismiss() }
        // Item "Import from gallery"
        view?.findViewById<ConstraintLayout>(R.id.import_from_gallery_button)?.setOnClickListener {
            if (MediaAccessHandler.checkPermissions(activity as MainActivity))
                MediaAccessHandler.openPhotosGallery(activity as MainActivity)
            else MediaAccessHandler.requestPermission(activity as MainActivity)
                builderAddMediaDialog.dismiss() }
    }

    /**
     * Initializes TextInputEdit fields with current [Estate] properties values to modify.
     */
    private fun updateFragmentViewsWithEstateProperties() {
        with(binding) {
            val currentEstate = listEstatesViewModel.selectedEstate ?: return
            nameSectionEdit.text = StringHandler.convertStringToEditable(currentEstate.type)
            locationSectionEdit.text = StringHandler.convertStringToEditable(currentEstate.location.address)
            descSectionEdit.text = StringHandler.convertStringToEditable(currentEstate.description)
            agentSectionEdit.text = StringHandler.convertStringToEditable("" +
                                 "${currentEstate.agent.firstName} ${currentEstate.agent.lastName}")
            priceSectionEdit.text = StringHandler.convertStringToEditable(currentEstate.price.toString())
            sliderSurface.value = currentEstate.interior.surface.toFloat()
            sliderRooms.value = currentEstate.interior.numberRooms.toFloat()
            sliderBathrooms.value = currentEstate.interior.numberBathrooms.toFloat()
            sliderBedrooms.value = currentEstate.interior.numberBedrooms.toFloat()
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
        val currentEstate = listEstatesViewModel.selectedEstate ?: return
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
            linearLayoutMedia.removeViews(0, currentEstate.listPhoto.size)
            tagContainerLayout.removeAllTags()
            updatePOIButtonTextDisplay()}
        currentEstate.listPhoto.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(AppInfo.DIALOG_RESET_KEY, builderResetEstateDialog.isShowing)
            putBoolean(AppInfo.DIALOG_CANCEL_KEY, builderCancelEstateDialog.isShowing)
            putBoolean(AppInfo.DIALOG_ADD_MEDIA_KEY, builderAddMediaDialog.isShowing)
            putBoolean(AppInfo.DIALOG_CONFIRM_MEDIA_KEY, builderNameMediaDialog.isShowing)
            putBoolean(AppInfo.DIALOG_LIST_AGENTS_KEY, builderListAgentsDialog.isShowing)
            putBoolean(AppInfo.UPDATE_ESTATE_KEY, updateEstate)
            putBoolean(AppInfo.DIALOG_ADD_POI_KEY, builderAddPOIDialog.isShowing)
            putInt(AppInfo.ESTATE_AGENT_KEY, agentSelected)
            putString(AppInfo.TEXT_DIALOG_CONFIRM_MEDIA_KEY, textNameMediaDialog)
            putInt(AppInfo.NUMBER_PHOTO_KEY, numberPhotosAdded)
            putBoolean(AppInfo.CONFIRM_EXIT_KEY, confirmExit)
            putBoolean(AppInfo.ERROR_SLIDERS_KEY, errorSliders)
            putBoolean(AppInfo.ERROR_SLIDERS_KEY, errorSliders)
        }
    }

    /**
     * Handles "Add photo" button.
     */
    private fun handleAddPhotoButton() {
        binding.buttonAddPhoto.setOnClickListener {
            if (!builderAddMediaDialog.isShowing) builderAddMediaDialog.show()
        }
    }

    /**
     * Handles clicks event on location edit text field.
     */
    private fun editLocation() {
        binding.locationSectionEdit.setOnClickListener {
            listEstatesViewModel.performAutocompleteRequest(activity as MainActivity)
        }
    }

    /**
     * Updates location estate displayed in the "Location" edit text view.
     */
    fun updateLocationDisplayed() {
        listEstatesViewModel.selectedEstate?.let {
            binding.locationSectionEdit.text =
                StringHandler.convertStringToEditable(it.location.address)
        }
    }

    /**
     * Gets a [Uri] photo value from Gallery or Camera.
     */
    fun addNewPhotoUri(uri: Uri?) {
        if (uri != null) {
            listEstatesViewModel.updatePhotoUri(uri.toString())
            displayNameMediaDialog()
        }
    }

    /**
     * Displays an [AlertDialog] for name photo entry.
     */
    private fun displayNameMediaDialog() {
        builderNameMediaDialog.apply {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }
    }

    /**
     * Restores the list of photo of an [Estate].
     */
    private fun updateHorizontalScrollViewWithPhotos() {
        listEstatesViewModel.selectedEstate?.let {
            it.listPhoto.forEach {  photo -> addNewFrameLayoutToBinding(photo) }
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
        if (binding.sliderBedrooms.value + binding.sliderBathrooms.value > binding.sliderRooms.value) {
            binding.apply {
                errorSliders = true
                sliderRooms.apply {
                    tickTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    trackActiveTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    trackInactiveTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google_light))
                }
                sliderBathrooms.apply {
                    tickTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    trackActiveTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    trackInactiveTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google_light))
                }
                sliderBedrooms.apply {
                    tickTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    trackActiveTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google))
                    trackInactiveTintList = ColorStateList.valueOf(resources.getColor(R.color.red_google_light))
                }
            }
        }
        else {
            errorSliders = false
            binding.apply {
                sliderRooms.apply {
                    tickTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    trackActiveTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    trackInactiveTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryLight))
                }
                sliderBathrooms.apply {
                    tickTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    trackActiveTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    trackInactiveTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryLight))
                }
                sliderBedrooms.apply {
                    tickTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    thumbTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    trackActiveTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    trackInactiveTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryLight))
                }
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
                val price: String = priceSectionEdit.text.toString()
                val nameAgent: String = agentSectionEdit.text.toString()
                if (name.isNotEmpty() && location.isNotEmpty() && nameAgent.isNotEmpty()
                    && description.isNotEmpty() && price.isNotEmpty()
                    && listEstatesViewModel.selectedEstate?.listPhoto?.isNotEmpty() == true
                    && !errorSliders) {
                    displayToastEstate(false)
                    (activity as MainActivity).notificationHandler.createNotification(updateEstate)
                    updateSelectedEstateFromViewModel(name, description, price)
                }
                else {
                    displayToastEstate(true)
                    displayErrorBoxMessage() }
            }
        }
    }

    /**
     * Displays [Toast] messages.
     */
    private fun displayToastEstate(error: Boolean) {
        if (error) // Error creating/modifying Estate
            Toast.makeText(context, resources.getString(R.string.str_toast_missing_information),
                Toast.LENGTH_LONG).show()
        else {
            // Modifications saved
            if (updateEstate) Toast.makeText(context, resources.getString(R.string.str_toast_estate_modified), Toast.LENGTH_LONG).show()
            // New estate created
            else Toast.makeText(context, resources.getString(R.string.str_toast_new_estate_created), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Update selected estate from view model which is used to store database modifications.
     * @param name : new name estate
     * @param description : new description estate
     * @param price : new price estate
     */
    private fun updateSelectedEstateFromViewModel(name: String, description:
                                                  String, price : String) {
        listEstatesViewModel.apply {
            selectedEstate?.apply {
                updateSelectedEstate(name, description, price.toInt())
                updateInteriorSelectedEstate(numberRooms = binding.sliderRooms.value.toInt(),
                                             numberBathrooms = binding.sliderBathrooms.value.toInt(),
                                             numberBedrooms = binding.sliderBedrooms.value.toInt(),
                                             surface = binding.sliderSurface.value.toInt())
                updateDateSelectedEstate(false)
                updateAgentSelectedEstate(agentSelected, updateEstate)
                updatePointOfInterestSelectedEstate(this@FragmentNewEstate.listPOI)}
             }
        confirmExit = true
        (activity as MainActivity).onBackPressed()
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
        binding.agentSectionEdit.setOnClickListener { builderListAgentsDialog.show() }
    }

    /**
     * Updates TextInputEditText field for agent selection.
     */
    private fun updateNameAgentEditText(position: Int, nameAgent: String?) {
        agentSelected = position
        binding.agentSectionEdit.text = Editable.Factory.getInstance().newEditable(nameAgent)
    }

    /**
     * Handles click events on "Points of interest" button.
     */
    private fun handleAddPOIButton() {
        binding.constraintLayoutAddPoi.setOnClickListener { builderAddPOIDialog.show() }
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
            listPOI.forEach { itPOI -> itContainer.addTag(itPOI) }
        }
    }

    /**
     * Initializes the list of POI used for container updates.
     */
    private fun initializeListPOI() {
        listEstatesViewModel.selectedEstate?.let { it ->
            it.listPointOfInterest.forEach { itPOI -> listPOI.add(itPOI.name) }
        }
    }
}