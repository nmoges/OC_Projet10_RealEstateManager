package com.openclassrooms.realestatemanager.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEstateDetailsBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

class FragmentEstateDetails : Fragment() {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_ESTATE_DETAILS"
        fun newInstance(): FragmentEstateDetails = FragmentEstateDetails()
    }

    /** binding parameter */
    private lateinit var binding: FragmentEstateDetailsBinding

    /** Contains a reference to a [ListEstatesViewModel]  */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains a reference an [Estate] selected by user to be displayed */
    private var selectedEstateToDisplay: Estate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())
                                                .get(ListEstatesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEstateDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize toolbar
        (activity as MainActivity).setToolbarProperties(
            R.string.str_toolbar_fragment_list_estate_title, true)
        selectedEstateToDisplay = listEstatesViewModel.selectedEstate
        updateViewsWithEstateProperties()
        updateHorizontalScrollViewWithPhotos()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_estate_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> { (activity as MainActivity).onBackPressed() }
            R.id.edit -> {
                (activity as MainActivity).displayFragmentNewEstate(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Initializes views with selected [Estate] properties values.
     */
    private fun updateViewsWithEstateProperties() {
        binding.apply {
            val surface: String = selectedEstateToDisplay?.interior?.surface.toString() + " sq m"
            surfaceValue.text = surface
            description.text = selectedEstateToDisplay?.description
            numberOfRoomsValue.text = selectedEstateToDisplay?.interior?.numberRooms.toString()
            numberOfBathroomsValue.text = selectedEstateToDisplay?.interior?.numberBathrooms.toString()
            numberOfBedroomsValue.text = selectedEstateToDisplay?.interior?.numberBedrooms.toString()
            addressLocationText.text = selectedEstateToDisplay?.address
        }
    }

    /**
     * Updates the horizontal scrollview with the list of photos associated to the selected
     * [Estate].
     */
    private fun updateHorizontalScrollViewWithPhotos() {
        selectedEstateToDisplay?.listPhoto?.forEach {
            val frameLayout: FrameLayout = MediaDisplayHandler.createNewFrameLayout(
                it, (activity as MainActivity))
            binding.linearLayout.addView(frameLayout)
        }
    }
}