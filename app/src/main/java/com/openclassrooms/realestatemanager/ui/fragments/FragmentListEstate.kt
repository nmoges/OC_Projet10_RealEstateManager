package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.data.model.Estate
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListEstateBinding
import com.openclassrooms.realestatemanager.ui.LayoutInflaterProvider
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.ui.adapters.ListEstatesAdapter
import com.openclassrooms.realestatemanager.viewmodels.DialogsViewModel
import com.openclassrooms.realestatemanager.viewmodels.EstateViewModel
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * [Fragment] subclass used to display the list of real estate.C
 */
@AndroidEntryPoint
class FragmentListEstate : Fragment() {

    companion object { fun newInstance(): FragmentListEstate = FragmentListEstate() }

    /** View Binding parameter */
    private lateinit var binding: FragmentListEstateBinding

    /** Contains a reference  to [ListEstatesViewModel] */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains a reference to a [EstateViewModel] */
    private lateinit var estateViewModel: EstateViewModel

    /** Contains a reference to a [DialogsViewModel] */
    private lateinit var dialogsViewModel: DialogsViewModel

    /** Defines an [AlertDialog] displaying a circular progress bar */
    private var builderLoadEstatesDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
        estateViewModel = ViewModelProvider(requireActivity())[EstateViewModel::class.java]
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentListEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)
        initializeRecyclerView()
        initializeProgressBarDialog()
        addObserverToViewModel()
        handleFloatingActionButton()
        checkDialogStatusInViewModel()
    }

    /**
     * Initializes an [AlertDialog.Builder] for [builderLoadEstatesDialog] property.
     */
    private fun initializeProgressBarDialog() {
        val viewProgressBarDialog: View? = LayoutInflaterProvider
            .getViewFromLayoutInflater(R.layout.dialog_progress_bar_launch, context)
        builderLoadEstatesDialog = AlertDialog.Builder(activity)
            .setView(viewProgressBarDialog)
            .create()
    }

    /**
     * Handles dialog display.
     * @param status : display status
     */
    fun displayBuilderLoadEstatesDialog(status: Boolean) {
        builderLoadEstatesDialog?.let {
            if (status) it.show() else it.dismiss()
        }
    }

    /**
     * Checks dialog display status stored in View Model
     */
    private fun checkDialogStatusInViewModel() {
        if (dialogsViewModel.loadEstatesDialogStatus) builderLoadEstatesDialog?.show()
    }

    /**
     * Handles click events on recycler view items.
     * @param position : position of the clicked item
     */
    @SuppressLint("NotifyDataSetChanged")
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
            else {
                handleFabVisibility(View.VISIBLE)
                activity.handleBackgroundGridVisibility(View.VISIBLE)
                activity.removeFragment(AppInfo.TAG_FRAGMENT_ESTATE_DETAILS)
            }
        }
    }

    /**
     * Clear "selected" status of the current selected item.
     */
    fun clearCurrentSelection() =
        (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).clearCurrentSelection()

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
    @SuppressLint("NotifyDataSetChanged")
    fun addObserverToViewModel() {
        // Observes SQLite database updates
        listEstatesViewModel.restoreData().observe(viewLifecycleOwner, {
            listEstatesViewModel.convertFullEstateInEstate(it)
        })
        // Observes listEstates updates
        listEstatesViewModel.listEstates.observe(viewLifecycleOwner, {
            (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).apply {
                builderLoadEstatesDialog?.let { if (it.isShowing) it.dismiss() }
                // Update list
                listEstates.apply {
                    clear()
                    addAll(it)
                }
                notifyDataSetChanged()
                // Update background text
                handleBackgroundMaterialTextVisibility(if (it.isNotEmpty()) View.INVISIBLE
                                                       else View.VISIBLE)
                checkIfListHasSelectedItem(it).let {
                    handleFabVisibility(if (it) View.INVISIBLE else View.VISIBLE)
                    activity.handleBackgroundGridVisibility(if (it) View.INVISIBLE else View.VISIBLE)
                }
            }
        })
    }

    /**
     * Checks if list of estates contains a selected item.
     * @param list : list of estates
     * @param : search status (false : no element found, true : selected item found)
     */
    private fun checkIfListHasSelectedItem(list: List<Estate>): Boolean {
        var status = false
        var index = 0
        while (index < list.size && !status) {
            if (list[index].selected) status = true
            else index++
        }
        return status
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
     * Handles click event on Floating Action Button.
     */
    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            estateViewModel.apply {
                typeOperation = false
                resetEstate()
            }
            (activity as MainActivity).displayFragmentNewEstate()
            handleFabVisibility(View.INVISIBLE)
            (activity as MainActivity).handleBackgroundGridVisibility(View.INVISIBLE)
        }
    }

    /**
     * Handle floating action button visibility.
     * @param visibility : Visibility status of the floating action button
     */
    fun handleFabVisibility(visibility: Int) {
        binding.fab.apply { if (visibility == View.VISIBLE) show() else hide() }
    }

    override fun onPause() {
        builderLoadEstatesDialog?.let { dialogsViewModel.loadEstatesDialogStatus = it.isShowing }
        super.onPause()
    }

    override fun onDestroy() {
        builderLoadEstatesDialog?.let { if (it.isShowing) it.dismiss() }
        super.onDestroy()
    }
}