package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.model.Photo
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [Fragment] subclass used to display a view allowing user to create
 * a new real estate.
 */
class FragmentNewEstate : Fragment() {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_NEW_ESTATE"
        const val DIALOG_RESET_KEY = "DIALOG_RESET_KEY"
        const val DIALOG_CANCEL_KEY = "DIALOG_CANCEL_KEY"
        const val DIALOG_ADD_MEDIA_KEY = "DIALOG_ADD_MEDIA_KEY"
        const val DIALOG_CONFIRM_MEDIA_KEY = "DIALOG_CONFIRM_MEDIA_KEY"
        const val UPDATE_ESTATE_KEY = "UPDATE_ESTATE_KEY"
        const val NAME_PHOTO_KEY: String = "NAME_PHOTO_KEY"
        const val URI_PHOTO_KEY: String = "URI_PHOTO_KEY"
        fun newInstance(): FragmentNewEstate = FragmentNewEstate()
    }

    /** View Binding parameter */
    lateinit var binding: FragmentNewEstateBinding

    /** Contains ViewModel reference */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /**  Selected [Estate] to modify */
    private lateinit var currentEstate: Estate

    /** Defines if current fragment is display for new estate creation (false) or to modify an
     * existing one (true). */
    var updateEstate: Boolean = false

    /** Defines an [AlertDialog] allowing user to reset [Estate] information. */
    private lateinit var builderResetEstateDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to add a new photo. */
    private lateinit var builderAddMediaDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to cancel creation or modification of an [Estate]. */
    private lateinit var builderCancelEstateDialog: AlertDialog

    /** Defines an [AlertDialog] allowing user to confirm photo addition. */
    private lateinit var builderNameMediaDialog: AlertDialog

    /**  Name of a selected photo */
    private var namePhoto: String = ""

    /** [Uri] of a selected photo */
    private var uriPhoto : String = ""

    /** Defines a [TextWatcher] for [builderNameMediaDialog] */
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(name: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(name: CharSequence?, start: Int, before: Int, count: Int) { }

        override fun afterTextChanged(name: Editable?) {
            if (name != null && builderNameMediaDialog != null) {
                builderNameMediaDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                      .isEnabled = name.isNotEmpty() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
            restoreDialogs(savedInstanceState)
            updateEstate = savedInstanceState.getBoolean(UPDATE_ESTATE_KEY)
            uriPhoto = savedInstanceState.getString(URI_PHOTO_KEY) ?: ""
            namePhoto = savedInstanceState.getString(NAME_PHOTO_KEY) ?: ""
        }
        updateToolbarTitle()
        updateMaterialButtonText()
        initializeViewModel()
        handleAddPhotoButton()
        handleSlidersListeners()
        handleConfirmationButtonListener()
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
     */
    private fun restoreDialogs(savedInstanceState: Bundle?) {
        when {
            savedInstanceState?.getBoolean(DIALOG_CANCEL_KEY) == true -> {
                builderCancelEstateDialog.show() }
            savedInstanceState?.getBoolean(DIALOG_RESET_KEY) == true -> {
                builderResetEstateDialog.show() }
            savedInstanceState?.getBoolean(DIALOG_ADD_MEDIA_KEY) == true -> {
                builderAddMediaDialog.show() }
            savedInstanceState?.getBoolean(DIALOG_CONFIRM_MEDIA_KEY) == true -> {
                builderNameMediaDialog.show()
                builderNameMediaDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
        }
    }

    /**
     * Creates [AlertDialog] for creation/update Estate cancellation, and reset
     * TextInputEdit fields.
     */
    private fun initializeDialogs() {
        initializeDialogResetEstate()
        initializeDialogCancelEstate()
        initializeDialogAddMedia()
        initializeDialogNameMedia()
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
     * Initializes an [AlertDialog.Builder] for [builderCancelEstateDialog] property.
     */
    private fun initializeDialogCancelEstate() {
        val title = if (updateEstate) resources.getString(
                                                      R.string.str_dialog_cancel_modification_title)
                    else resources.getString(R.string.str_dialog_cancel_creation_title)
        val message = if (updateEstate) resources.getString(
                                                   R.string.str_dialog_cancel_modifications_message)
                      else resources.getString(R.string.str_dialog_cancel_creation_message)
        builderCancelEstateDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) {_, _ ->
                builderCancelEstateDialog.dismiss()
                (activity as MainActivity).onBackPressed() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_no)) {_, _ -> }
            .create()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderAddMediaDialog] property.
     */
    private fun initializeDialogAddMedia() {
        val viewAddMediaDialog: View? = getViewFromLayoutInflater(R.layout.dialog_media_selection)
        builderAddMediaDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_add_media_title))
            .setView(viewAddMediaDialog)
            .create()
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
                namePhoto = textInputEditText?.text.toString()
                // Add new Photo object to the Estate list of photos
                currentEstate.listPhoto.add(0, createNewPhoto())
                addNewFrameLayoutToBinding(currentEstate.listPhoto[0])
                textInputEditText?.text?.clear() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) {_, _ ->
                textInputEditText?.text?.clear() }
            .create()
    }


    private fun createNewPhoto(): Photo {
        val bitmap = MediaDisplayHandler.createBitmap(uriPhoto.toUri(), activity as MainActivity)
        val convertedPhoto = MediaDisplayHandler.bitmapToString(bitmap)
        return Photo(convertedPhoto, namePhoto)
    }


    private fun addNewFrameLayoutToBinding(photo: Photo) {
        val frameLayout: FrameLayout = MediaDisplayHandler
            .createNewFrameLayout(photo, activity as MainActivity)
        binding.linearLayoutMedia.addView(frameLayout, 0)
    }

    private fun getViewFromLayoutInflater(@LayoutRes layout: Int): View? {
        val inflater: LayoutInflater? =
                       context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater
        return inflater?.inflate(layout, null)
    }

    /**
     * Handles click interactions with option from "Add media" dialog
     */
    private fun handleAddMediaDialogButtons(view: View?) {
        // Item "Take picture"
        view?.findViewById<MaterialButton>(R.id.take_picture_button)?.setOnClickListener {
            builderAddMediaDialog.dismiss() }
        // Item "Import from gallery"
        view?.findViewById<MaterialButton>(R.id.import_gallery_button)?.setOnClickListener {
            (activity as? MainActivity)?.openPhotosGallery()
            builderAddMediaDialog.dismiss() }
    }

    /**
     * Initializes observer to [MainActivity] viewModel.
     */
    private fun initializeViewModel() {
        listEstatesViewModel = (activity as MainActivity).listEstatesViewModel
        listEstatesViewModel.selectedEstate.observe(viewLifecycleOwner, {
            currentEstate = it
            // Restore data for an existing Estate
            if (updateEstate) updateFragmentViewsWithEstateProperties()
            restoreListPhoto()
        })
    }

    /**
     * Initializes TextInputEdit fields with current [Estate] properties values to modify.
     */
    private fun updateFragmentViewsWithEstateProperties() {
        fun convertStringToEditable(text: String): Editable =
            Editable.Factory.getInstance().newEditable(text)

        binding.newEstateNameSectionTextInputEdit.text =
            convertStringToEditable(currentEstate.type)
        binding.newEstateLocationSectionTextInputEdit.text =
            convertStringToEditable(currentEstate.address)
        binding.newEstateDescSectionTextInputEdit.text =
            convertStringToEditable(currentEstate.description)
        binding.newEstateAgentSectionTextInputEdit.text =
            convertStringToEditable(currentEstate.nameAgent)
        binding.newEstatePriceSectionTextInputEdit.text =
            convertStringToEditable(currentEstate.price.toString())
        binding.sliderSurface.value = currentEstate.interior.surface.toFloat()
        binding.sliderRooms.value = currentEstate.interior.numberRooms.toFloat()
        binding.sliderBathrooms.value = currentEstate.interior.numberBathrooms.toFloat()
        binding.sliderBedrooms.value = currentEstate.interior.numberBedrooms.toFloat()
    }

    /**
     * Updates MaterialButton text according to [updateEstate] value.
     */
    private fun updateMaterialButtonText() {
        if (updateEstate) binding.confirmationButton.text =
             resources.getString(R.string.str_button_confirmation_modification)
        else binding.confirmationButton.text =
             resources.getString(R.string.str_button_confirmation_creation)
    }

    /**
     * Updates [MainActivity] toolbar title according to [updateEstate] value.
     */
    private fun updateToolbarTitle() {
        if (updateEstate) (activity as MainActivity)
        .setToolbarProperties(R.string.str_toolbar_fragment_modify_estate_title, true)
        else (activity as MainActivity)
        .setToolbarProperties(R.string.str_toolbar_fragment_new_estate_title, true)
    }

    /**
     * Clears all views in fragment.
     */
    private fun confirmReset() {
        binding.newEstateNameSectionTextInputEdit.text?.clear()
        binding.newEstateLocationSectionTextInputEdit.text?.clear()
        binding.newEstateDescSectionTextInputEdit.text?.clear()
        binding.newEstateAgentSectionTextInputEdit.text?.clear()
        binding.newEstatePriceSectionTextInputEdit.text?.clear()
        binding.sliderSurface.value = 200.0F
        binding.sliderRooms.value = 5.0F
        binding.sliderBathrooms.value = 1.0F
        binding.sliderBedrooms.value = 1.0F
        binding.linearLayoutMedia.removeViews(0, currentEstate.listPhoto.size)
        currentEstate.listPhoto.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(DIALOG_RESET_KEY, builderResetEstateDialog.isShowing)
        outState.putBoolean(DIALOG_CANCEL_KEY, builderCancelEstateDialog.isShowing)
        outState.putBoolean(DIALOG_ADD_MEDIA_KEY, builderAddMediaDialog.isShowing)
        outState.putBoolean(UPDATE_ESTATE_KEY, updateEstate)
        outState.putBoolean(DIALOG_CONFIRM_MEDIA_KEY, builderNameMediaDialog.isShowing)
        outState.putString(NAME_PHOTO_KEY, namePhoto)
        outState.putString(URI_PHOTO_KEY, uriPhoto)
    }

    fun dismissDialogOnBackPressed(): Boolean {
        if (builderCancelEstateDialog.isShowing) {
            builderCancelEstateDialog.dismiss()
            return true }
        return false
    }

    private fun handleAddPhotoButton() {
        binding.buttonAddPhoto.setOnClickListener {
            if (!builderAddMediaDialog.isShowing) builderAddMediaDialog.show() }
    }

    /**
     * Adds a new converted [Uri] to the property [listPhotosUri] of [currentEstate].
     */
    fun addNewPhoto(uri: Uri?) {
        if (uri != null) {
            uriPhoto = uri.toString()
            builderNameMediaDialog.show()
            builderNameMediaDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
    }

    /**
     * Restores the list of photo of an [Estate].
     */
    private fun restoreListPhoto() {
        if (currentEstate.listPhoto.size > 0) {
            for (i in currentEstate.listPhoto.size-1 downTo  0)
                addNewFrameLayoutToBinding(currentEstate.listPhoto[i])
        }
    }

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

    private fun handleConfirmationButtonListener() {
        binding.confirmationButton.setOnClickListener {
            val name: String = binding.newEstateNameSectionTextInputEdit.text.toString()
            val location: String = binding.newEstateLocationSectionTextInputEdit.text.toString()
            val description: String = binding.newEstateDescSectionTextInputEdit.text.toString()

            val price: String = binding.newEstatePriceSectionTextInputEdit.text.toString()
            val nameAgent: String = binding.newEstateAgentSectionTextInputEdit.text.toString()
            val size: Int = currentEstate.listPhoto.size

            if (name.isNotEmpty() && location.isNotEmpty()
                && description.isNotEmpty()  && price.isNotEmpty()
                && nameAgent.isNotEmpty() && size != 0) {
                displayToastEstate(false)
                createEstate(name, location, description, nameAgent, price)
            }
            else displayToastEstate(true)
        }
    }

    private fun displayToastEstate(error: Boolean) {
        if (error) // Error creating/modifying Estate
            Toast.makeText(context, resources.getString(R.string.str_toast_missing_information),
                           Toast.LENGTH_LONG).show()
        else {
            if (updateEstate) // Modifications saved
                Toast.makeText(context, resources.getString(R.string.str_toast_estate_modified),
                               Toast.LENGTH_LONG).show()
            else // New estate created
                Toast.makeText(context, resources.getString(R.string.str_toast_new_estate_created),
                               Toast.LENGTH_LONG).show()
        }
    }

    private fun createEstate(name: String, location: String, description: String, nameAgent: String, price : String) {
        val surface: Int = binding.sliderSurface.value.toInt()
        val rooms: Int = binding.sliderRoomsValue.text.toString().toInt()
        val bathrooms: Int = binding.sliderBathroomsValue.text.toString().toInt()
        val bedrooms: Int = binding.sliderBedroomsValue.text.toString().toInt()
        currentEstate.type = name
        currentEstate.address = location
        currentEstate.description = description
        currentEstate.nameAgent = nameAgent
        currentEstate.interior.numberRooms = rooms
        currentEstate.interior.numberBathrooms = bathrooms
        currentEstate.interior.numberBedrooms = bedrooms
        currentEstate.interior.surface = surface
        currentEstate.price = price.toInt()
        listEstatesViewModel.updateViewModel(currentEstate, updateEstate)
        (activity as MainActivity).onBackPressed()
    }
}