package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentSettingsBinding
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.viewmodels.CurrencyViewModel

class FragmentSettings : Fragment() {

    companion object {
        const val TAG = "TAG_FRAGMENT_SETTINGS"
        const val DIALOG_CURRENCY_SELECT_KEY = "DIALOG_CURRENCY_SELECT_KEY"
        const val DIALOG_DELETE_ACCOUNT_KEY = "DIALOG_DELETE_ACCOUNT_KEY"
        fun newInstance(): FragmentSettings = FragmentSettings()
    }

    /** View Binding parameter */
    lateinit var binding: FragmentSettingsBinding

    private lateinit var builderCurrencySelectionDialog: AlertDialog
    private lateinit var builderDeleteAccountDialog: AlertDialog
    private lateinit var currencyViewModel: CurrencyViewModel
    private lateinit var filePreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        currencyViewModel = ViewModelProvider(requireActivity())
                                             .get(CurrencyViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize toolbar
        (activity as MainActivity).setToolbarProperties(
            R.string.str_item_settings, true)
        filePreferences = (activity as MainActivity).getSharedPreferences(AppInfo.FILE_SHARED_PREF,
                                                                               Context.MODE_PRIVATE)
        initializeCardViewsDisplay()
        initializeCurrencySelectionDialog()
        initializeDeleteAccountDialog()
        initCurrencyCardView()
        handleClickCurrencyCardListener()
        handleClicksDeleteAccountCardListener()
        if (savedInstanceState != null) restoreDialog(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (activity as MainActivity).onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun initializeCardViewsDisplay() {
        binding.apply {
            cardViewCurrency.setBackgroundResource(R.drawable.background_card_view_unclicked)
            cardViewDeleteAccount.setBackgroundResource(R.drawable.background_card_view_unclicked)
        }
    }

    /**
     * Handles click of currency selection option card.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun handleClickCurrencyCardListener() {
        binding.cardViewCurrency.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cardViewCurrency.setBackgroundResource(R.drawable.background_card_view_clicked)
                    true }
                MotionEvent.ACTION_UP -> {
                    binding.cardViewCurrency.setBackgroundResource(R.drawable.background_card_view_unclicked)
                    builderCurrencySelectionDialog.show()
                    true
                }
                else -> { true }
            }
        }
    }

    /**
     * Handles click of delete account option card.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun handleClicksDeleteAccountCardListener() {
        binding.cardViewDeleteAccount.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cardViewDeleteAccount.setBackgroundResource(R.drawable.background_card_view_clicked)
                    true }
                MotionEvent.ACTION_UP -> {
                    binding.cardViewDeleteAccount.setBackgroundResource(R.drawable.background_card_view_unclicked)
                    builderDeleteAccountDialog.show()
                    true
                }
                else -> { true }
            }
        }
    }
    /**
     * Initializes currency symbol displayed.
     */
    private fun initCurrencyCardView() {
        val symbol = filePreferences.getString(AppInfo.PREF_CURRENCY, "$")
        binding.currencyTextSymbol.text = symbol
    }

    /**
     * Initializes an [AlertDialog.Builder] displaying the list of currencies available.
     */
    private fun initializeCurrencySelectionDialog() {
        val inflater: LayoutInflater? = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        as? LayoutInflater

        val viewCurrencySelection: View? = inflater?.inflate(R.layout.dialog_currency_selection, null)

        builderCurrencySelectionDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_title_select_currency_dialog))
            .setView(viewCurrencySelection)
            .create()

        handleCurrencySelectionDialogButtons(viewCurrencySelection)
    }

    private fun initializeDeleteAccountDialog() {
        builderDeleteAccountDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_title_delete_account_dialog))
            .setMessage(resources.getString(R.string.str_text_delete_account_dialog))
            .setPositiveButton(resources.getString(R.string.str_title_button_confirm)) { _, _ ->  }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) { _, _ ->  }
            .create()
    }

    /**
     * Handles interactions with dialog items.
     * @param view: View in which the dialog is attached
     */
    private fun handleCurrencySelectionDialogButtons(view: View?) {
        // EURO currency button
        view?.findViewById<ConstraintLayout>(R.id.constraint_layout_view_euro_currency)
            ?.setOnClickListener {
                updateCurrency("EUR")
            }
        // USD currency button
        view?.findViewById<ConstraintLayout>(R.id.constraint_layout_view_usd_currency)
            ?.setOnClickListener {
                updateCurrency( "USD")
            }
    }

    private fun updateCurrency(currency: String) {
        // Update currency in shared preferences file
        val editor: SharedPreferences.Editor = filePreferences.edit()
        editor.putString(AppInfo.PREF_CURRENCY, currency).apply()

        // Update card view display
        updateDisplayedCurrencyInCardView(currency)

        // Update in ViewModel
        postNewCurrencyInViewModel(currency)
        builderCurrencySelectionDialog.dismiss()
    }

    private fun restoreDialog(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getBoolean(DIALOG_CURRENCY_SELECT_KEY) == true) {
            builderCurrencySelectionDialog.show()
        }
        if (savedInstanceState?.getBoolean(DIALOG_DELETE_ACCOUNT_KEY) == true) {
            builderDeleteAccountDialog.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(DIALOG_CURRENCY_SELECT_KEY, builderCurrencySelectionDialog.isShowing)
            putBoolean(DIALOG_DELETE_ACCOUNT_KEY, builderDeleteAccountDialog.isShowing)
        }
    }

    private fun updateDisplayedCurrencyInCardView(selectedCurrency: String) {
        binding.currencyTextSymbol.text = selectedCurrency
    }

    private fun postNewCurrencyInViewModel(currency: String) {
        currencyViewModel.updateCurrencySelected(currency)
    }
}