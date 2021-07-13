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

    private lateinit var binding: FragmentEstateDetailsBinding
    private lateinit var listEstatesViewModel: ListEstatesViewModel
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
        binding.description.text = selectedEstateToDisplay?.description

        val surface: String = selectedEstateToDisplay?.interior?.surface.toString() + " sq m"
        binding.surfaceValue.text = surface

        binding.numberOfRoomsValue.text = selectedEstateToDisplay?.interior?.numberRooms.toString()
        binding.numberOfBathroomsValue.text = selectedEstateToDisplay?.interior?.numberBathrooms.toString()
        binding.numberOfBedroomsValue.text = selectedEstateToDisplay?.interior?.numberBedrooms.toString()
        binding.addressLocationText.text = selectedEstateToDisplay?.address
    }

    private fun updateHorizontalScrollViewWithPhotos() {
        selectedEstateToDisplay?.listPhoto?.forEach {
            val frameLayout: FrameLayout = MediaDisplayHandler.createNewFrameLayout(
                it, (activity as MainActivity))
            binding.linearLayout.addView(frameLayout)
        }
    }
}