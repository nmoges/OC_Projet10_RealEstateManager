package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

class FragmentEstateDetails : Fragment() {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_ESTATE_DETAILS"
        fun newInstance(): FragmentEstateDetails = FragmentEstateDetails()
    }

    lateinit var binding: FragmentEstateDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_estate_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize toolbar
        (activity as MainActivity).setToolbarProperties(
                                                    R.string.str_toolbar_fragment_list_estate_title,
                                       true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_estate_details, menu)
    }
}