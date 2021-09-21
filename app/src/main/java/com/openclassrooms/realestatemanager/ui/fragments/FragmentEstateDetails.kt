package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.openclassrooms.data.model.Estate
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEstateDetailsBinding
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.MapHandler
import com.openclassrooms.realestatemanager.utils.ProgressBarHandler
import com.openclassrooms.realestatemanager.viewmodels.DialogsViewModel
import com.openclassrooms.realestatemanager.viewmodels.EstateViewModel
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [Fragment] subclass used to display the details of a selected existing [Estate].
 */
class FragmentEstateDetails : Fragment() {

    companion object { fun newInstance(): FragmentEstateDetails = FragmentEstateDetails() }

    /** binding parameter */
    private lateinit var binding: FragmentEstateDetailsBinding

    /** Contains a reference to a [ListEstatesViewModel]  */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains a reference to a [DialogsViewModel] */
    private lateinit var dialogsViewModel: DialogsViewModel

    /** Contains a reference to a [EstateViewModel] */
    private lateinit var estateViewModel: EstateViewModel

    /** Contains a reference an [Estate] selected by user to be displayed */
    private var selectedEstateToDisplay: Estate? = null

    /** Contains an [AlertDialog.Builder] reference to display a confirmation message */
    private var builderConfirmDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())[ListEstatesViewModel::class.java]
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
        estateViewModel = ViewModelProvider(requireActivity())[EstateViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentEstateDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setToolbarProperties(
            R.string.str_toolbar_fragment_list_estate_title, true)
        selectedEstateToDisplay = listEstatesViewModel.selectedEstate
        updateViewsWithEstateProperties()
        updateHorizontalScrollViewWithPhotos()
        handleSaleButtonListener()
        initializeSaleButtonDisplay()
        initializeConfirmationDialog()
        checkDialogStatusInViewModel()
        updateMaterialTextSaleStatus()
        displayPublishDate()
        displayLocationOnMap()
        initializeTagContainerDisplay()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_estate_details, menu)
        initializeMenuIcons(menu)
    }

    /**
     * Display location estate on a static map.
     */
    private fun displayLocationOnMap() {
        context?.let {
            CoroutineScope(Dispatchers.Main).launch {
                selectedEstateToDisplay?.location?.apply {
                    val uri = MapHandler.getLocationEstateUri(latitude, longitude, 400, 19)
                    Glide.with(activity as MainActivity)
                        .load(uri)
                        .centerCrop()
                        .placeholder(ProgressBarHandler.getProgressBarDrawable(it))
                        .override(binding.mapView.width, binding.mapView.height)
                        .into(binding.mapView)
                }
            }
        }
    }


    /**
     * Enables/disables "edit" menu icon display according to the [Estate] status (available or sold)
     * @param menu : menu to initialize
     */
    private fun initializeMenuIcons(menu: Menu) {
        val status = selectedEstateToDisplay?.status
        status?.let { menu[0].isVisible = !it }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                dialogsViewModel.confirmDialogStatus = false
                (activity as MainActivity).onBackPressed()
            }
            R.id.edit -> {
                estateViewModel.apply {
                    typeOperation = true
                    selectedEstateToDisplay?.let { initializeWithSelectedEstateValues(it) }
                }
                (activity as MainActivity).displayFragmentNewEstate()
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
            addressLocationText.text = selectedEstateToDisplay?.location?.address
        }
    }

    /**
     * Updates the horizontal scrollview with the list of photos associated to the selected
     * [Estate].
     */
    private fun updateHorizontalScrollViewWithPhotos() {
        selectedEstateToDisplay?.listPhoto?.asReversed()?.forEach {
            val frameLayout: FrameLayout = MediaDisplayHandler.createNewFrameLayout(
                it, (activity as MainActivity))
            binding.linearLayout.addView(frameLayout)
        }
    }

    /**
     * Initializes sale button display.
     */
    private fun initializeSaleButtonDisplay() {
        binding.saleButton.visibility = if (selectedEstateToDisplay?.status == true) View.GONE
                                        else View.VISIBLE
    }

    /**
     * Handles sale button interactions.
     */
    private fun handleSaleButtonListener() {
        binding.saleButton.setOnClickListener {
            dialogsViewModel.confirmDialogStatus = true
            builderConfirmDialog?.show()
        }
    }

    /**
     * Updates sale status of the estate in database.
     */
    private fun updateSaleStatusInDb() {
        selectedEstateToDisplay?.let { itEstate ->
            estateViewModel.initializeWithSelectedEstateValues(itEstate)
            estateViewModel.updateDateEstate(true)
            estateViewModel.getNewEstate((activity as MainActivity).getFirebaseAuth())
                .observe(viewLifecycleOwner, { itUpdatedEstate ->
                listEstatesViewModel.updateDatabase(true, itUpdatedEstate)
            })
        }
    }

    /**
     * Updates sale status display.
     */
    private fun updateMaterialTextSaleStatus() {
        val status = selectedEstateToDisplay?.status
        if (status != null)
            if (status) displaySaleDate()
    }

    /**
     * Updates "publish date" MaterialTextView.
     */
    private fun displayPublishDate() {
        selectedEstateToDisplay?.run {
            val entryDateText = dates.dateEntry
            val textToDisplay = resources.getString(R.string.str_published_on) + ": $entryDateText"
            binding.publishDate.text = textToDisplay
        }
    }

    /**
     * Updates "sale date" MaterialTextView.
     */
    private fun displaySaleDate() {
        selectedEstateToDisplay?.run {
            val saleDateText = dates.dateSale
            if (saleDateText.isNotEmpty()) {
                val textToDisplay = resources.getString(R.string.str_sold_status) + ": $saleDateText"
                binding.saleStatus.apply {
                    text = textToDisplay
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        setTextColor(resources.getColor(R.color.red_google, null))
                    else
                        setTextColor(resources.getColor(R.color.red_google))
                }
            }
        }
    }

    /**
     * Initializes an [AlertDialog.Builder] to display a confirmation message to user.
     */
    private fun initializeConfirmationDialog() {
        builderConfirmDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_dialog_confirm_sale_tile))
            .setMessage(resources.getString(R.string.str_dialog_confirm_sale_message))
            .setPositiveButton(resources.getString(R.string.str_dialog_button_yes))
            { _, _ ->
                selectedEstateToDisplay?.status = true
                binding.saleButton.visibility = View.INVISIBLE
                updateSaleStatusInDb()
                updateMaterialTextSaleStatus()
            }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel))
            { _, _ ->  }
            .create()
    }

    /**
     * Checks if a dialog was displayed before configuration change.
     */
    private fun checkDialogStatusInViewModel() {
        if (dialogsViewModel.confirmDialogStatus) builderConfirmDialog?.show()
    }

    /**
     * Initializes the tag container to display all points of interest associated with the
     * current [Estate].
     */
    private fun initializeTagContainerDisplay() {
        binding.tagContainerLayout.let { itContainer ->
            selectedEstateToDisplay?.listPointOfInterest?.forEach { itPoi ->
                itContainer.addTag(itPoi.name)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        builderConfirmDialog?.let { dialogsViewModel.confirmDialogStatus = it.isShowing }
    }

    override fun onDestroy() {
        super.onDestroy()
        builderConfirmDialog?.let { if (it.isShowing) it.dismiss() }
    }
}