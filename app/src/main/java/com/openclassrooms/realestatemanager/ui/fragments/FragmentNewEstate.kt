package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.MediaAccessHandler
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Photo
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [Fragment] subclass used to display a view allowing user to create a new real estate.
 */
class FragmentNewEstate : Fragment() {
    companion object { fun newInstance(): FragmentNewEstate = FragmentNewEstate() }

    /** View Binding parameter */
    lateinit var binding: FragmentNewEstateBinding

    /** Contains ViewModel reference */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Defines if current fragment is display for new estate creation (false) or to modify an
     * existing one (true). */
    var updateEstate: Boolean = false

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
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(name: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(name: CharSequence?, start: Int, before: Int, count: Int) { }
        override fun afterTextChanged(name: Editable?) {
            if (name != null) {
                textNameMediaDialog = name.toString()
                builderNameMediaDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .isEnabled = name.isNotEmpty() }
        }
    }

    /** Defines [AlertDialog] for list of agents access */
    private lateinit var builderListAgentsDialog: AlertDialog

    /** Contains temporary value of a selected agent */
    private var agentSelected = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity()).get(ListEstatesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentNewEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeDialogs()
        if (savedInstanceState != null) {
            confirmExit = savedInstanceState.getBoolean(AppInfo.CONFIRM_EXIT_KEY, false)
            numberPhotosAdded = savedInstanceState.getInt(AppInfo.NUMBER_PHOTO_KEY, 0)
            textNameMediaDialog =
                   savedInstanceState.getString(AppInfo.TEXT_DIALOG_CONFIRM_MEDIA_KEY, "")
            restoreDialogs(savedInstanceState)
            updateEstate = savedInstanceState.getBoolean(AppInfo.UPDATE_ESTATE_KEY, false)
            agentSelected = savedInstanceState.getInt(AppInfo.ESTATE_AGENT_KEY, 1)
        }
        updateToolbarTitle()
        updateMaterialButtonText()
        if (updateEstate) updateFragmentViewsWithEstateProperties()
        updateHorizontalScrollViewWithPhotos()
        handleAddPhotoButton()
        handleSlidersListeners()
        handleConfirmationButtonListener()
        handleTextInputEditTextWatchers()
        handleNameAgentTextInputEditListener()
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
                .isEnabled = textNameMediaDialog.isNotEmpty()
        }
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
            val arrayAdapter: ArrayAdapter<String> =
                                           ArrayAdapter(context,android.R.layout.select_dialog_item)
            val list = listEstatesViewModel.listAgents.value
            if (list != null) {
                list.forEach { arrayAdapter.add("${it.firstName} ${it.lastName}") }
                builderListAgentsDialog = AlertDialog.Builder(activity)
                    .setTitle("Select agent")
                    .setAdapter(arrayAdapter) { _, which ->
                     updateNameAgentTextInputEditText(which+1, arrayAdapter.getItem(which)) }
                    .create()
            }
        }
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
                (activity as MainActivity).onBackPressed()
            }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_no)) {_, _ -> }.create()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderAddMediaDialog] property.
     */
    private fun initializeDialogAddMedia() {
        val viewAddMediaDialog: View? = getViewFromLayoutInflater(R.layout.dialog_media_selection)
        builderAddMediaDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_add_media_title))
            .setView(viewAddMediaDialog).create()
        handleAddMediaDialogButtons(viewAddMediaDialog)
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderNameMediaDialog] property.
     */
    private fun initializeDialogNameMedia() {
        val viewNameMediaDialog: View? = getViewFromLayoutInflater(R.layout.dialog_media_confirmation)
        val textInputEditText: TextInputEditText? =
            viewNameMediaDialog?.findViewById(R.id.new_media_text_input_edit)
        textInputEditText?.addTextChangedListener(textWatcher)
        builderNameMediaDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_name_media_title))
            .setView(viewNameMediaDialog)
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) {_, _ ->
                addNewPhoto()
                textInputEditText?.text?.clear() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) {_, _ ->
                textInputEditText?.text?.clear() }
            .create()
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
     * Gets layout inflater using context
     * @param layout : layout to inflate
     * @return : inflated view
     */
    private fun getViewFromLayoutInflater(@LayoutRes layout: Int): View? {
        val inflater: LayoutInflater? =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater
        return inflater?.inflate(layout, null)
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
        fun convertStringToEditable(text: String): Editable =
            Editable.Factory.getInstance().newEditable(text)
        with(binding) {
            val currentEstate = listEstatesViewModel.selectedEstate ?: return
            newEstateNameSectionTextInputEdit.text = convertStringToEditable(currentEstate.type)
            newEstateLocationSectionTextInputEdit.text = convertStringToEditable(currentEstate.address)
            newEstateDescSectionTextInputEdit.text = convertStringToEditable(currentEstate.description)
            newEstateAgentSectionTextInputEdit.text =
            convertStringToEditable("${currentEstate.agent.firstName} ${currentEstate.agent.lastName}")
            newEstatePriceSectionTextInputEdit.text =
                                             convertStringToEditable(currentEstate.price.toString())
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
            if (updateEstate)
            setToolbarProperties(R.string.str_toolbar_fragment_modify_estate_title, true)
            else
            setToolbarProperties(R.string.str_toolbar_fragment_new_estate_title, true)
        }
    }

    /**
     * Clears all views in fragment.
     */
    private fun confirmReset() {
        val currentEstate = listEstatesViewModel.selectedEstate ?: return
        with(binding) {
            newEstateNameSectionTextInputEdit.text?.clear()
            newEstateLocationSectionTextInputEdit.text?.clear()
            newEstateDescSectionTextInputEdit.text?.clear()
            newEstateAgentSectionTextInputEdit.text?.clear()
            newEstatePriceSectionTextInputEdit.text?.clear()
            sliderSurface.value = 200.0F
            sliderRooms.value = 5.0F
            sliderBathrooms.value = 1.0F
            sliderBedrooms.value = 1.0F
            linearLayoutMedia.removeViews(0, currentEstate.listPhoto.size)
        }
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
            putInt(AppInfo.ESTATE_AGENT_KEY, agentSelected)
            putString(AppInfo.TEXT_DIALOG_CONFIRM_MEDIA_KEY, textNameMediaDialog)
            putInt(AppInfo.NUMBER_PHOTO_KEY, numberPhotosAdded)
            putBoolean(AppInfo.CONFIRM_EXIT_KEY, confirmExit)
        }
    }

    /**
     * Handles "Add photo" button.
     */
    private fun handleAddPhotoButton() {
        binding.buttonAddPhoto.setOnClickListener {
            if (!builderAddMediaDialog.isShowing) builderAddMediaDialog.show() }
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
        fun getSliderString(maxValue: Int, currentValue: Int, @StringRes resMaxValue: Int,
                            @StringRes resValue: Int?, type: Boolean): String {
            return if (currentValue == maxValue) { resources.getString(resMaxValue, currentValue) }
            else { if (type && resValue != null) resources.getString(resValue, currentValue)
            else currentValue.toString() }
        }
        binding.sliderSurface.addOnChangeListener { _, _, _ ->
            val text = getSliderString(5000, binding.sliderSurface.value.toInt(),
                R.string.str_sqm_unit_greater_than_or_equal, R.string.str_sqm_unit, true)
            binding.sliderSurfaceValue.text = text }
        binding.sliderRooms.addOnChangeListener { _, _, _ ->
            val text = getSliderString(20, binding.sliderRooms.value.toInt(),
                R.string.str_greater_than_or_equal, null, false)
            binding.sliderRoomsValue.text = text }
        binding.sliderBathrooms.addOnChangeListener { _, _, _ ->
            val text = getSliderString(5, binding.sliderBathrooms.value.toInt(),
                R.string.str_greater_than_or_equal, null, false)
            binding.sliderBathroomsValue.text = text }
        binding.sliderBedrooms.addOnChangeListener { _, _, _ ->
            val text = getSliderString(10, binding.sliderBedrooms.value.toInt(),
                R.string.str_greater_than_or_equal, null, false)
            binding.sliderBedroomsValue.text = text }
    }

    /**
     * Handle user interactions with confirmation button.
     */
    private fun handleConfirmationButtonListener() {
        binding.confirmationButton.setOnClickListener {
            val name: String = binding.newEstateNameSectionTextInputEdit.text.toString()
            val location: String = binding.newEstateLocationSectionTextInputEdit.text.toString()
            val description: String = binding.newEstateDescSectionTextInputEdit.text.toString()
            val price: String = binding.newEstatePriceSectionTextInputEdit.text.toString()
            if (name.isNotEmpty() && location.isNotEmpty()
                && description.isNotEmpty() && price.isNotEmpty()
                && listEstatesViewModel.selectedEstate?.listPhoto?.isNotEmpty() == true) {
                displayToastEstate(false)
                updateSelectedEstateFromViewModel(name, location, description, price)
            }
            else {
                displayToastEstate(true)
                displayErrorBoxMessage()
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
     * @param location : new location estate
     * @param description : new description estate
     * @param price : new price estate
     */
    private fun updateSelectedEstateFromViewModel(name: String, location: String, description: String, price : String) {
        listEstatesViewModel.apply {
            selectedEstate?.apply {
                updateSelectedEstate(name, location, description, price.toInt())
                updateInteriorSelectedEstate(
                    numberRooms = binding.sliderRoomsValue.text.toString().toInt(),
                    numberBathrooms = binding.sliderBathroomsValue.text.toString().toInt(),
                    numberBedrooms = binding.sliderBedroomsValue.text.toString().toInt(),
                    surface = binding.sliderSurface.value.toInt())
                addAgentToSelectedEstate(agentSelected, updateEstate)
                }
             }
        confirmExit = true
        (activity as MainActivity).onBackPressed()
    }

    /**
     * Handles display of error message of TextInputLayouts.
     */
    private fun displayErrorBoxMessage() {
        binding.apply {
            if (newEstateNameSectionTextInputEdit.text?.isEmpty() == true)
                newEstateNameSectionTextInputLayout.apply {
                    isErrorEnabled = true
                    error = "Empty" }
            if (newEstateLocationSectionTextInputEdit.text?.isEmpty() == true)
                newEstateLocationSectionTextInputLayout.apply {
                    isErrorEnabled = true
                    error = "Empty" }
            if (newEstateDescSectionTextInputEdit.text?.isEmpty() == true)
                newEstateDescSectionTextInputLayout.apply {
                    isErrorEnabled = true
                    error = "Empty" }
            if (newEstatePriceSectionTextInputEdit.text?.isEmpty() == true)
                newEstatePriceSectionTextInputLayout.apply {
                    isErrorEnabled = true
                    error = "Empty" }
            if (newEstateAgentSectionTextInputEdit.text?.isEmpty() == true)
                newEstateAgentSectionTextInputLayout.apply {
                    isErrorEnabled = true
                    error = "Empty" }
        }
    }

    /**
     * Handles TextInputEditText views text watchers.
     */
    private fun handleTextInputEditTextWatchers() {
        binding.apply {
            newEstateNameSectionTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    newEstateNameSectionTextInputLayout.isErrorEnabled = false } })
            newEstateLocationSectionTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    newEstateLocationSectionTextInputLayout.isErrorEnabled = false } })
            newEstateDescSectionTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    newEstateDescSectionTextInputLayout.isErrorEnabled = false } })
            newEstatePriceSectionTextInputEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    newEstatePriceSectionTextInputLayout.isErrorEnabled = false } })
        }
    }

    /**
     * Handle user interactions with TextInputEditText field for agent selection.
     */
    private fun handleNameAgentTextInputEditListener() {
        binding.newEstateAgentSectionTextInputEdit.setOnClickListener {
            builderListAgentsDialog.show()
        }
    }

    /**
     * Updates TextInputEditText field for agent selection.
     */
    private fun updateNameAgentTextInputEditText(position: Int, nameAgent: String?) {
        agentSelected = position
        binding.newEstateAgentSectionTextInputEdit.text =
                                               Editable.Factory.getInstance().newEditable(nameAgent)
    }

}