package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.ui.adapters.ListEstatesAdapter
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel

/**
 * [Fragment] subclass used to display the list of real estate.
 */
class FragmentListEstate : Fragment() {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_LIST_ESTATE"
        fun newInstance(): FragmentListEstate = FragmentListEstate()
    }

    private lateinit var binding: FragmentListEstateBinding
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize toolbar
        (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)

        initializeRecyclerView()
        initializeViewModel()
    }


    private fun handleClickOnEstateItem(position: Int) {
        (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).apply {
            clearPreviousSelection(position)
            val status: Boolean = updateItemSelectionStatus(position)
            notifyDataSetChanged()

             if (status) {
                 (activity as MainActivity).handleFabVisibility(View.INVISIBLE)
                 (activity as MainActivity).handleBackgroundGridVisibility(View.INVISIBLE)
                 // Add current selected item to view model
                 listEstatesViewModel.setSelectedEstate(position)
                 // Display FragmentDetails with selected item
                 (activity as MainActivity).displayFragmentDetails()
             }
             else (activity as MainActivity).onBackPressed()
        }
    }


    fun clearCurrentSelection() {
        (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).clearCurrentSelection()
    }


    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_list_estate, menu)

        if (menu is MenuBuilder) {
            val menuBuilder: MenuBuilder = menu
            menuBuilder.setOptionalIconsVisible(true)
        }
    }

    private fun initializeViewModel() {
        // Initialize viewModel
        listEstatesViewModel = (activity as MainActivity).listEstatesViewModel
        listEstatesViewModel.listEstates.observe(viewLifecycleOwner, {
            (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).apply {
                resetSelection(it)
                // Update list
                listEstates.clear()
                listEstates.addAll(it)
                notifyDataSetChanged()
                // Update background text
                handleBackgroundMaterialTextVisibility(if (listEstates.size > 0) View.INVISIBLE
                                                       else View.INVISIBLE)
            }
        })
    }

    private fun initializeRecyclerView() {
        binding.recyclerViewListEstates.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ListEstatesAdapter { handleClickOnEstateItem(it) }
        }
    }

    private fun handleBackgroundMaterialTextVisibility(visibility: Int) {
        binding.txtBackgroundNoRealEstate.visibility = visibility
    }

    private fun resetSelection(listEstate: List<Estate>) {
        val orientation: Boolean = (activity as MainActivity).typeOrientation
        val itemSelected: Boolean = parentFragmentManager.findFragmentByTag(FragmentEstateDetails.TAG) == null

        if ((activity as MainActivity).typeLayout) {
            if (!orientation && !itemSelected) {
                for (i in listEstate.indices) listEstate[i].selected = false
            }
        }
        else {
            for (i in listEstate.indices) { listEstate[i].selected = false }
        }
    }
}