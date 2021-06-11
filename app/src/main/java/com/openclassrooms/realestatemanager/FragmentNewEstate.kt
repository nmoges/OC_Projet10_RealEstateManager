package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.databinding.FragmentNewEstateBinding

/**
 * [Fragment] subclass used to display a view allowing user to create
 * a new real estate.
 */
class FragmentNewEstate : Fragment() {

    companion object {
        const val TAG: String = "TAG_FRAGMENT_NEW_ESTATE"
        fun newInstance(): FragmentNewEstate = FragmentNewEstate()
    }

    lateinit var binding: FragmentNewEstateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeToolbar()
    }

    private fun initializeToolbar() {
        (activity as MainActivity).setToolbarTitle(R.string.str_toolbar_fragment_new_estate_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_new_estate, menu)
    }
}