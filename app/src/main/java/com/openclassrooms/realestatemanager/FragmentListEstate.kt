package com.openclassrooms.realestatemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.databinding.FragmentListEstateBinding

/**
 * [Fragment] subclass used to display the list of real estate.
 */
class FragmentListEstate : Fragment() {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_LIST_ESTATE"
        fun newInstance(): FragmentListEstate = FragmentListEstate()
    }

    lateinit var binding: FragmentListEstateBinding

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
        initializeToolbar()
    }

    private fun initializeToolbar() {
        (activity as MainActivity).setToolbarTitle(R.string.str_toolbar_fragment_list_estate_title)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_list_estate, menu)


        if (menu is MenuBuilder) {
            val menuBuilder: MenuBuilder = menu
            menuBuilder.setOptionalIconsVisible(true)
        }
    }
}