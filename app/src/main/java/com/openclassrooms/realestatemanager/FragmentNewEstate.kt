package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewEstateBinding.inflate(inflater, container, false)
        return binding.root
    }
}