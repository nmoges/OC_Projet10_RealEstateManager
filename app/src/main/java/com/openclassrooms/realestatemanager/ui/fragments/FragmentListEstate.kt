package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListEstateBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.ui.adapters.ListEstatesAdapter
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * [Fragment] subclass used to display the list of real estate.
 */
@AndroidEntryPoint
class FragmentListEstate : Fragment() {

    companion object { fun newInstance(): FragmentListEstate = FragmentListEstate() }

    /** View Binding parameter */
    private lateinit var binding: FragmentListEstateBinding

    /** Contains a reference  to [ListEstatesViewModel] */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentListEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize toolbar
        (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)
        initializeRecyclerView()
        addObserverToViewModel()
        handleFloatingActionButton()
    }

    /**
     * Handles click events on recycler view items.
     * @param position : position of the clicked item
     */
    fun handleClickOnEstateItem(position: Int) {
        (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).apply {
            clearPreviousSelection(position)
            val status: Boolean = updateItemSelectionStatus(position)
            notifyDataSetChanged()
            if (status) {
                handleFabVisibility(View.INVISIBLE)
                activity.handleBackgroundGridVisibility(View.INVISIBLE)
                // Add current selected item to view model
                listEstatesViewModel.setSelectedEstate(position)
                // Display FragmentDetails with selected item
                activity.displayFragmentDetails()
            }
            else activity.onBackPressed()
        }
    }

    /**
     * Clear "selected" status of the current selected item.
     */
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_agent -> { (activity as MainActivity).showDialogAddAgent() }
            R.id.search -> { (activity as MainActivity).displayFragmentSearch() }
            R.id.settings -> {
                (activity as MainActivity).apply {
                    handleFabVisibility(View.INVISIBLE)
                    handleBackgroundGridVisibility(View.INVISIBLE)
                    displayFragmentSettings()
                }
            }
            R.id.map -> {
                (activity as MainActivity).apply {
                    handleFabVisibility(View.INVISIBLE)
                    handleBackgroundGridVisibility(View.INVISIBLE)
                    displayFragmentMap()
                }
            }
            R.id.logout -> { (activity as MainActivity).showDialogLogout() }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Handles [listEstatesViewModel] observer.
     */
    private fun addObserverToViewModel() {
        listEstatesViewModel.listEstates.observe(viewLifecycleOwner, {
            (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).apply {
                resetSelection(it)
                // Update list
                listEstates.apply {
                    clear()
                    addAll(it)
                }
                notifyDataSetChanged()
                // Update background text
                handleBackgroundMaterialTextVisibility(if (listEstates.size > 0) View.INVISIBLE
                                                       else View.VISIBLE)
            }
        })
    }

    /**
     * Initializes fragment recycler view.
     */
    private fun initializeRecyclerView() {
        binding.recyclerViewListEstates.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ListEstatesAdapter(context) { handleClickOnEstateItem(it) }
            (adapter as ListEstatesAdapter).activity = (activity as MainActivity)
            (adapter as ListEstatesAdapter).updateCurrencyWithSharedPreferencesValue()
        }
    }

    /**
     * Handles text background visibility.
     * @param visibility : visibility value
     */
    private fun handleBackgroundMaterialTextVisibility(visibility: Int) {
        binding.txtBackgroundNoRealEstate.visibility = visibility
    }

    /**
     * Resets list item selection status.
     * @param listEstate : list of [Estate]
     */
    private fun resetSelection(listEstate: List<Estate>) {
        val orientation: Boolean = (activity as MainActivity).typeOrientation
        val itemSelected: Boolean = parentFragmentManager
                                     .findFragmentByTag(AppInfo.TAG_FRAGMENT_ESTATE_DETAILS) == null
        if ((activity as MainActivity).typeLayout) {
            if (!orientation && !itemSelected) {
                for (i in listEstate.indices) listEstate[i].selected = false
            }
        }
        else { for (i in listEstate.indices) { listEstate[i].selected = false } }
    }

    /**
     * Handles click event on Floating Action Button.
     */
    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            listEstatesViewModel.createNewEstate()
            (activity as MainActivity).displayFragmentNewEstate(false)
            handleFabVisibility(View.INVISIBLE)
            (activity as MainActivity).handleBackgroundGridVisibility(View.INVISIBLE)
        }
    }

    /**
     * Handle floating action button visibility.
     * @param visibility : Visibility status of the floating action button
     */
    fun handleFabVisibility(visibility: Int) =
        binding.fab.apply { if (visibility == View.VISIBLE) show() else hide() }
}