package com.openclassrooms.realestatemanager.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding
import com.openclassrooms.realestatemanager.ui.dialogs.ResetEstateDialog
import com.openclassrooms.realestatemanager.ui.dialogs.ResetEstateDialogCallback

/**
 * [Fragment] subclass used to display a view allowing user to create
 * a new real estate.
 */
class FragmentNewEstate : Fragment(), ResetEstateDialogCallback {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_NEW_ESTATE"
        const val NAME_KEY: String = "NAME_KEY"
        const val LOCATION_KEY: String = "LOCATION_KEY"
        const val DESCRIPTION_KEY: String = "DESCRIPTION_KEY"
        const val SURFACE_KEY: String = "SURFACE_KEY"
        const val ROOMS_KEY: String = "ROOMS_KEY"
        const val BATHROOMS_KEY: String = "BATHROOMS_KEY"
        const val BEDROOMS_KEY: String = "BEDROOMS_KEY"
        fun newInstance(): FragmentNewEstate = FragmentNewEstate()
    }

    lateinit var binding: FragmentNewEstateBinding


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
        if (savedInstanceState != null) {
            restoreTextInputEditValues(savedInstanceState)
            if (parentFragmentManager.findFragmentByTag(ResetEstateDialog.TAG) != null) {
                val dialog: ResetEstateDialog = (parentFragmentManager
                                     .findFragmentByTag(ResetEstateDialog.TAG)) as ResetEstateDialog
                dialog.callback = this
            }
        }
        // Initialize toolbar
        (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_new_estate_title, true)
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
     * Displays a [ResetEstateDialog] to user.
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NAME_KEY, binding.newEstateNameSectionTextInputEdit.text.toString())
        outState.putString(LOCATION_KEY, binding.newEstateLocationSectionTextInputEdit.text.toString())
        outState.putString(DESCRIPTION_KEY, binding.newEstateDescSectionTextInputEdit.text.toString())
        outState.putString(SURFACE_KEY, binding.newEstateSurfaceSectionTextInputEdit.text.toString())
        outState.putString(ROOMS_KEY, binding.newEstateNbRoomsSectionTextInputEdit.text.toString())
        outState.putString(BATHROOMS_KEY, binding.newEstateNbBathroomsSectionTextInputEdit.text.toString())
        outState.putString(BEDROOMS_KEY, binding.newEstateNbBedroomsSectionTextInputEdit.text.toString())
    }

    private fun restoreTextInputEditValues(savedInstanceState: Bundle?) {
        binding.newEstateNameSectionTextInputEdit.setText(savedInstanceState?.getString(NAME_KEY))
    }
}