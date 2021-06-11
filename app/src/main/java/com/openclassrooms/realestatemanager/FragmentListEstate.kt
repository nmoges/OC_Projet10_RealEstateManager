package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListEstateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}