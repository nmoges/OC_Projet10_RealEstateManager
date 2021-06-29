package com.openclassrooms.realestatemanager.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.ui.dialogs.ResetEstateDialog
import com.openclassrooms.realestatemanager.ui.dialogs.ResetEstateDialogCallback
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [Fragment] subclass used to display a view allowing user to create
 * a new real estate.
 */
class FragmentNewEstate : Fragment(), ResetEstateDialogCallback {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_NEW_ESTATE"
        const val NAME_KEY: String = "NAME_KEY"
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
        if (savedInstanceState != null) restoreDialogCallback()
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
            android.R.id.home -> { (activity as MainActivity).onBackPressed() }
            R.id.reset -> { displayResetDescriptionDialog() }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Restores [ResetEstateDialog] dialog callback after a configuration change.
     */
    private fun restoreDialogCallback() {
        if (parentFragmentManager.findFragmentByTag(ResetEstateDialog.TAG) != null) {
            val dialog: ResetEstateDialog = (parentFragmentManager
                .findFragmentByTag(ResetEstateDialog.TAG)) as ResetEstateDialog
            dialog.callback = this
        }
    }

    /**
     * Displays a [ResetEstateDialog] dialog to user.
     */
    private fun displayResetDescriptionDialog() {
        val dialog = ResetEstateDialog(this)
        dialog.show((activity as MainActivity).supportFragmentManager, ResetEstateDialog.TAG)
    }

    /**
     * Clears all TextInputEdit fields.
     */
    override fun confirmReset() {
        binding.newEstateNameSectionTextInputEdit.text?.clear()
        binding.newEstateLocationSectionTextInputEdit.text?.clear()
        binding.newEstateDescSectionTextInputEdit.text?.clear()
        binding.newEstateSurfaceSectionTextInputEdit.text?.clear()
        binding.newEstateNbRoomsSectionTextInputEdit.text?.clear()
        binding.newEstateNbBathroomsSectionTextInputEdit.text?.clear()
        binding.newEstateNbBedroomsSectionTextInputEdit.text?.clear()
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
        binding.newEstateNameSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.type)

        binding.newEstateLocationSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.address)

        binding.newEstateDescSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.description)

        binding.newEstateSurfaceSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.surface.toString())

        binding.newEstateNbRoomsSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.numberRooms.toString())

        binding.newEstateNbBathroomsSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.numberBathrooms.toString())

        binding.newEstateNbBedroomsSectionTextInputEdit.text =
        Editable.Factory.getInstance().newEditable(selectedEstateToDisplay.numberBedrooms.toString())
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
}