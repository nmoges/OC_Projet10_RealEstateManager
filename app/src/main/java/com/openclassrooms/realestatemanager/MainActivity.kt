package com.openclassrooms.realestatemanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeToolbar()
        handleFloatingActionButton()
    }

    private fun initializeToolbar() {
        binding.toolbar.title = resources.getString(R.string.main_activity_toolbar_title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.toolbar.setTitleTextColor(resources.getColor(R.color.white, null))
        else
            binding.toolbar.setTitleTextColor(resources.getColor(R.color.white))
    }

    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                                  .replace(R.id.fragment_container_view,
                                           FragmentNewEstate.newInstance(),
                                           FragmentNewEstate.TAG)
                                  .commit()
            binding.fab.hide()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) != null) {
            supportFragmentManager.beginTransaction()
                                  .replace(R.id.fragment_container_view,
                                           FragmentListEstate.newInstance(),
                                           FragmentListEstate.TAG)
                .commit()
            binding.fab.show()
        }
        else super.onBackPressed()
    }
}