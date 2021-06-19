package com.openclassrooms.realestatemanager.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.fragments.FragmentEstateDetails
import com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate
import com.openclassrooms.realestatemanager.ui.fragments.FragmentNewEstate

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val FAB_STATUS_KEY = "FAB_STATUS_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) restoreViews(savedInstanceState)

        initializeToolbar()
        handleFloatingActionButton()
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            binding.toolbar.setTitleTextColor(resources.getColor(R.color.white, null))
        else
            binding.toolbar.setTitleTextColor(resources.getColor(R.color.white))
    }

    override fun setToolbarProperties(@StringRes title: Int, backIconDisplay: Boolean) {
        supportActionBar?.title = resources.getString(title)
        supportActionBar?.setDisplayHomeAsUpEnabled(backIconDisplay)
        if (backIconDisplay)
            supportActionBar?.setHomeAsUpIndicator(ResourcesCompat.getDrawable(resources,
                R.drawable.ic_baseline_arrow_back_24dp_white,
                null))
    }

    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            launchFragmentTransaction(FragmentNewEstate.newInstance(), FragmentNewEstate.TAG)
            binding.fab.hide()
        }
    }

   private fun launchFragmentTransaction(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, tag)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) != null ||
            supportFragmentManager.findFragmentByTag(FragmentEstateDetails.TAG) != null) {
            // Restore list fragment
            launchFragmentTransaction(FragmentListEstate.newInstance(), FragmentListEstate.TAG)
            // Restore floating action button
            binding.fab.show()
        }
        else finishAffinity() // Close app
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save state
        binding.fab.let { outState.putInt(FAB_STATUS_KEY, it.visibility) }
    }

    /**
     * Restore all [MainActivity] views states after a configuration change.
     * @param savedInstanceState: [Bundle]
     */
    private fun restoreViews(savedInstanceState: Bundle) {
        val visibility: Int = savedInstanceState.getInt(FAB_STATUS_KEY)
        if (visibility == View.VISIBLE) binding.fab?.show()
        else binding.fab?.hide()
    }
}