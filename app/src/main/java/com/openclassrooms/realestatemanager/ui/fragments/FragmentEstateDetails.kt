package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEstateDetailsBinding
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.ui.MediaDisplayHandler
import com.openclassrooms.realestatemanager.viewmodels.ListEstatesViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * [Fragment] subclass used to display the details of a selected existing [Estate].
 */
class FragmentEstateDetails : Fragment() {

    companion object { fun newInstance(): FragmentEstateDetails = FragmentEstateDetails() }

    /** binding parameter */
    private lateinit var binding: FragmentEstateDetailsBinding

    /** Contains a reference to a [ListEstatesViewModel]  */
    private lateinit var listEstatesViewModel: ListEstatesViewModel

    /** Contains a reference an [Estate] selected by user to be displayed */
    private var selectedEstateToDisplay: Estate? = null

    /** Contains an [AlertDialog.Builder] reference to display a confirmation message */
    private lateinit var builderConfirmDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        listEstatesViewModel = ViewModelProvider(requireActivity())
                                                .get(ListEstatesViewModel::class.java)
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
        updateMaterialTextSaleStatus()
        updatePublishDateToDisplay()
        updateSaleDateToDisplay()
        restoreDialog(savedInstanceState)
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
            builderConfirmDialog.show()
        }
    }

    /**
     * Updates sale status of the estate in database.
     */
    private fun updateSaleStatusInDb() {
        listEstatesViewModel.selectedEstate = selectedEstateToDisplay
        listEstatesViewModel.addDateToSelectedEstate(true)
        listEstatesViewModel.updateDatabase(true)
    }

    /**
     * Updates sale status display.
     */
    private fun updateMaterialTextSaleStatus() {
        val status = selectedEstateToDisplay?.status
        if (status != null) {
            val text = resources.getString(R.string.str_sold_status)
            if (status) binding.saleStatus.apply {
                this.text = text
                setTextColor(resources.getColor(R.color.red_google))
            }
        }
    }

    /**
     * Updates "publish date" MaterialTextView.
     */
    private fun updatePublishDateToDisplay() {
        selectedEstateToDisplay?.run {
            val publishDate = getDate(mutableListOf(dates.entryDate.entryDateDay,
                                                           dates.entryDate.entryDateMonth,
                                                           dates.entryDate.entryDateYear))
            val textToDisplay = resources.getString(R.string.str_published_on) + ": $publishDate"
            binding.publishDate.text = textToDisplay
        }
    }

    /**
     * Updates "sale date" MaterialTextView.
     */
    private fun updateSaleDateToDisplay() {
        selectedEstateToDisplay?.run {
            val saleDate = getDate(mutableListOf(dates.saleDate.saleDateDay,
                                                      dates.saleDate.saleDateMonth,
                                                      dates.saleDate.saleDateYear))
            val textToDisplay = resources.getString(R.string.str_sold_status) + ": $saleDate"
            binding.saleStatus.text = textToDisplay
        }
    }

    //TODO() : To move
    @SuppressLint("SimpleDateFormat")
    private fun getDate(list: MutableList<Int>): String {
        val calendar = Calendar.getInstance()
        calendar.set(list[2], list[1], list[0])

        val simpleDateFormat: SimpleDateFormat = when (Locale.getDefault().language) {
            "en" -> { SimpleDateFormat("MM/dd/yyyy") }
            else -> { SimpleDateFormat("dd/MM/yyyy") }
        }
        return simpleDateFormat.format(calendar.time)
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
     * Restores [builderConfirmDialog] if displayed before a configuration change.
     * @param savedInstanceState : Bundle
     */
    private fun restoreDialog(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getBoolean(AppInfo.DIALOG_CONFIRM_SELL_KEY) == true) {
            builderConfirmDialog.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(AppInfo.DIALOG_CONFIRM_SELL_KEY, builderConfirmDialog.isShowing)
        }
    }
}