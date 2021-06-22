package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.R
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
        // Initialize toolbar
        (activity as MainActivity)
            .setToolbarProperties(R.string.str_toolbar_fragment_list_estate_title, false)

        binding.buttonTest.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if ((activity as MainActivity).window.decorView
                                          .findViewById<View>(R.id.fragment_container_view) != null)
                    (activity as MainActivity).launchTransaction(
                        R.id.fragment_container_view,
                        FragmentEstateDetails.newInstance(),
                        FragmentEstateDetails.TAG)
                else (activity as MainActivity).launchTransaction(
                        R.id.fragment_container_view_right,
                        FragmentEstateDetails.newInstance(),
                        FragmentEstateDetails.TAG)
            }
            else (activity as MainActivity).launchTransaction(
                    R.id.fragment_container_view,
                    FragmentEstateDetails.newInstance(),
                    FragmentEstateDetails.TAG)
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
}