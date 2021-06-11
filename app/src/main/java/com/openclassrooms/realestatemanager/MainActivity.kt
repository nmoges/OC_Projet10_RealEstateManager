package com.openclassrooms.realestatemanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding

/**
 * [AppCompatActivity] subclass which defines the main activity of the application.
 * This activity contains all existing fragments.
 */
class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding

    companion object {
        val FAB_STATUT_KEY = "FAB_STATUS_KEY"
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

    override fun setToolbarTitle(@StringRes title: Int) {
        binding.toolbar.title = resources.getString(title)
    }

    private fun handleFloatingActionButton() {
        binding.fab.setOnClickListener {
            launchFragmentTransaction(FragmentNewEstate.newInstance(), FragmentNewEstate.TAG)
            binding.fab.hide()
        }
    }

    private fun launchFragmentTransaction(fragment: Fragment, TAG: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, TAG)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(FragmentNewEstate.TAG) != null) {
            // Restore list fragment
            launchFragmentTransaction(FragmentListEstate.newInstance(), FragmentListEstate.TAG)
            // Restore floating action button
            binding.fab.show()
            // Restore toolbar title
            setToolbarTitle(R.string.str_toolbar_fragment_list_estate_title)
        }
        else super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save state
        outState.putInt(FAB_STATUT_KEY, binding.fab.visibility)
    }

    /**
     * Restore all [MainActivity] views states after a configuration change.
     * @param savedInstanceState: [Bundle]
     */
    private fun restoreViews(savedInstanceState: Bundle) {
        val visibility: Int = savedInstanceState.getInt(FAB_STATUT_KEY)
        if (visibility == View.VISIBLE) binding.fab.show()
        else binding.fab.hide()
    }
}