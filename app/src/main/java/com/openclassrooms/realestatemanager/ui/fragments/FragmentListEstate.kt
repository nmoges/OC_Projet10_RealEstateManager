package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListEstateBinding
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
        handleClickOnEstateItem()
    }


    private fun handleClickOnEstateItem() {
        (binding.recyclerViewListEstates.adapter as ListEstatesAdapter)
            .onItemClickListener = {
            Log.i("ITEM_CLICK", "Type : ${it.type}")
            Log.i("ITEM_CLICK", "Description : ${it.description}")
            Log.i("ITEM_CLICK", "Address : ${it.address}" )
        }
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
        listEstatesViewModel = ViewModelProvider(this).get(ListEstatesViewModel::class.java)
        listEstatesViewModel.getEstates().observe(viewLifecycleOwner, {
            (binding.recyclerViewListEstates.adapter as ListEstatesAdapter).apply {
                // Update list
                listEstates.addAll(it)
                notifyDataSetChanged()
                // Update background text
                val visibility: Int = if (listEstates.size > 0) View.INVISIBLE else View.INVISIBLE
                handleBackgroundMaterialTextVisibility(visibility)
            }
        })
    }

    private fun initializeRecyclerView() {
        binding.recyclerViewListEstates.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ListEstatesAdapter()
        }
    }

    private fun handleBackgroundMaterialTextVisibility(visibility: Int) {
        binding.txtBackgroundNoRealEstate.visibility = visibility
    }
}