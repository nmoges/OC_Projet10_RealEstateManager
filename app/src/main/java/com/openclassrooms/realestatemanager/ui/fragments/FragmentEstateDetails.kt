package com.openclassrooms.realestatemanager.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R

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
        menu.clear()
        inflater.inflate(R.menu.menu_fragment_estate_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> { (activity as MainActivity).onBackPressed() }
            R.id.edit -> { }
        }
        return super.onOptionsItemSelected(item)
    }
}