package com.openclassrooms.realestatemanager.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentSettingsBinding
import com.openclassrooms.realestatemanager.ui.activities.MainActivity

class FragmentSettings : Fragment() {

    companion object {
        const val TAG = "TAG_FRAGMENT_SETTINGS"
        const val DIALOG_CURRENCY_SELECT_KEY = "DIALOG_CURRENCY_SELECT_KEY"
        fun newInstance(): FragmentSettings = FragmentSettings()
    }

    /** View Binding parameter */
    lateinit var binding: FragmentSettingsBinding

    private lateinit var builderCurrencySelectionDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeCurrencySelectionDialog()
        handleClickCurrencyCardListener()

        if (savedInstanceState != null) restoreDialog(savedInstanceState)
    }

    /**
     * Handles click of currency selection option card.
     */
    private fun handleClickCurrencyCardListener() {
        binding.cardViewCurrency.setOnClickListener {
            builderCurrencySelectionDialog.show()
        }
    }

    /**
     * Initializes an [AlertDialog.Builder] displaying the list of currencies available.
     */
    private fun initializeCurrencySelectionDialog() {
        val inflater: LayoutInflater? = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        as? LayoutInflater

        val viewCurrencySelection: View? = inflater?.inflate(R.layout.dialog_currency_selection, null)

        builderCurrencySelectionDialog = AlertDialog.Builder(activity)
            .setTitle("Select currency")
            .setView(viewCurrencySelection)
            .create()

        handleCurrencySelectionDialogButtons(viewCurrencySelection)
    }

    /**
     * Handles interactions with dialog items.
     * @param view: View in which the dialog is attached
     */
    private fun handleCurrencySelectionDialogButtons(view: View?) {
        val filePreferences: SharedPreferences = (activity as MainActivity).getSharedPreferences(
            AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = filePreferences.edit()

        // EURO currency button
        view?.findViewById<ConstraintLayout>(R.id.constraint_layout_view_euro_currency)?.setOnClickListener {
            editor.putString(AppInfo.PREF_CURRENCY, "EUR").apply()
            builderCurrencySelectionDialog.dismiss()
        }

        // USD currency button
        view?.findViewById<ConstraintLayout>(R.id.constraint_layout_view_usd_currency)?.setOnClickListener {
            editor.putString(AppInfo.PREF_CURRENCY, "USD").apply()
            builderCurrencySelectionDialog.dismiss()
        }
    }

    private fun restoreDialog(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getBoolean(DIALOG_CURRENCY_SELECT_KEY) == true)
            builderCurrencySelectionDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(DIALOG_CURRENCY_SELECT_KEY, builderCurrencySelectionDialog.isShowing)
    }
}