package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.model.Estate

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
        const val UPDATE_ESTATE_KEY = "UPDATE_ESTATE_KEY"
        fun newInstance(): FragmentNewEstate = FragmentNewEstate()
    }

    /**
     * View Binding parameter
     */
    lateinit var binding: FragmentNewEstateBinding

    /**
     * Contains ViewModel reference
     */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /**
     * Selected Estate to modify
     */
    private lateinit var selectedEstateToDisplay: Estate

    /**
     * Defines if current fragment is display for new estate creation (false) or to modify an
     * existing one (true)
     */
    var updateEstate: Boolean = false

    private lateinit var builderResetEstateDialog: AlertDialog
    lateinit var builderCancelEstateDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
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
        }
        updateToolbarTitle()
        updateMaterialButtonText()
        if (updateEstate) initializeViewModel()
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
     * Restores [ResetEstateDialog] dialog callback after a configuration change.
     */

    private fun restoreDialogs(savedInstanceState: Bundle?) {
        when {
            savedInstanceState?.getBoolean(DIALOG_CANCEL_KEY) == true -> {
                builderCancelEstateDialog.show()
            }
            savedInstanceState?.getBoolean(DIALOG_RESET_KEY) == true -> {
                builderResetEstateDialog.show()
            }
        }
    }

    /**
     * Creates [AlertDialog] for creation/update Estate cancellation, and reset
     * TextInputEdit fields.
     */
    private fun initializeDialogs() {
        // Dialog Reset Estate information
        builderResetEstateDialog =  AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_reset_title))
            .setMessage(resources.getString(R.string.str_dialog_reset_message))
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) { _, _ -> confirmReset() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_no)) { _, _ -> }
            .create()

        // Dialog Cancel Estate creation/modification
        val title = if (updateEstate) resources.getString(R.string.str_dialog_cancel_modification_title)
        else resources.getString(R.string.str_dialog_cancel_creation_title)
        val message = if (updateEstate) resources.getString(R.string.str_dialog_cancel_modifications_message)
        else resources.getString(R.string.str_dialog_cancel_creation_message)
        builderCancelEstateDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes)) {_, _ ->
                builderCancelEstateDialog.dismiss()
                (activity as MainActivity).onBackPressed()
            }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_no)) {_, _ -> }
            .create()
    }

    /**
     * Initializes observer to [MainActivity] viewModel.
     */
    private fun initializeViewModel() {
        listEstatesViewModel = (activity as MainActivity).listEstatesViewModel
        listEstatesViewModel.selectedEstate.observe(viewLifecycleOwner, {
            selectedEstateToDisplay = it
            updateTextInputEditWithEstateProperties()
        })
    }

    /**
     * Initializes TextInputEdit fields with current [Estate] properties values to modify.
     */
    private fun updateTextInputEditWithEstateProperties() {
        fun convertStringToEditable(text: String): Editable =
            Editable.Factory.getInstance().newEditable(text)

        binding.newEstateNameSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.type)

        binding.newEstateLocationSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.address)

        binding.newEstateDescSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.description)

        binding.newEstateSurfaceSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.surface.toString())

        binding.newEstateNbRoomsSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.numberRooms.toString())

        binding.newEstateNbBathroomsSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.numberBathrooms.toString())

        binding.newEstateNbBedroomsSectionTextInputEdit.text =
            convertStringToEditable(selectedEstateToDisplay.numberBedrooms.toString())
    }

    /**
     * Updates MaterialButton text according to [updateEstate] value.
     */
    private fun updateMaterialButtonText() {
        if (updateEstate)
            binding.confirmationButton.text =
                resources.getString(R.string.str_button_confirmation_modification)
        else
            binding.confirmationButton.text =
                resources.getString(R.string.str_button_confirmation_creation)
    }

    /**
     * Updates [MainActivity] toolbar title according to [updateEstate] value.
     */
    private fun updateToolbarTitle() {
        if (updateEstate)
            (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_modify_estate_title, true)
        else
            (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_new_estate_title, true)
    }

    /**
     * [ResetEstateDialogCallback] interface implementation
     * Clears all TextInputEdit fields.
     */
    private fun confirmReset() {
        binding.newEstateNameSectionTextInputEdit.text?.clear()
        binding.newEstateLocationSectionTextInputEdit.text?.clear()
        binding.newEstateDescSectionTextInputEdit.text?.clear()
        binding.newEstateSurfaceSectionTextInputEdit.text?.clear()
        binding.newEstateNbRoomsSectionTextInputEdit.text?.clear()
        binding.newEstateNbBathroomsSectionTextInputEdit.text?.clear()
        binding.newEstateNbBedroomsSectionTextInputEdit.text?.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(DIALOG_RESET_KEY, builderResetEstateDialog.isShowing)
        outState.putBoolean(DIALOG_CANCEL_KEY, builderCancelEstateDialog.isShowing)
        outState.putBoolean(UPDATE_ESTATE_KEY, updateEstate)
    }

    fun dismissDialogOnBackPressed(): Boolean {
        if (builderCancelEstateDialog.isShowing) {
            builderCancelEstateDialog.dismiss()
            return true
        }
        return false
    }
}