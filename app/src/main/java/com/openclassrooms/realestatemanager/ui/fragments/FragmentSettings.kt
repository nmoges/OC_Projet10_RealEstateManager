package com.openclassrooms.realestatemanager.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentSettingsBinding
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.viewmodels.DialogsViewModel

/**
 * [Fragment] subclass used to display the settings of the RealEstateManager application.
 */
class FragmentSettings : Fragment() {

    companion object { fun newInstance(): FragmentSettings = FragmentSettings() }

    /** View Binding parameter */
    private lateinit var binding: FragmentSettingsBinding

    /** Dialogs */
    private lateinit var builderCurrencySelectionDialog: AlertDialog
    private lateinit var builderDeleteAccountDialog: AlertDialog

    /** Contains a reference to a [DialogsViewModel] */
    private lateinit var dialogsViewModel: DialogsViewModel

    /** SharedPreferences contains the saved currency value */
    private lateinit var filePreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dialogsViewModel = ViewModelProvider(requireActivity())[DialogsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
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
        checkDialogStatusInViewModel()
        initCurrencyCardView()
        handleClickCurrencyCardListener()
        handleClicksDeleteAccountCardListener()
        if (savedInstanceState != null) restoreDialog(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (activity as MainActivity).onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Initializes views.
     */
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
                    binding.cardViewDeleteAccount
                           .setBackgroundResource(R.drawable.background_card_view_clicked)
                    true }
                MotionEvent.ACTION_UP -> {
                    binding.cardViewDeleteAccount
                           .setBackgroundResource(R.drawable.background_card_view_unclicked)
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

    /**
     * Initializes an [AlertDialog.Builder] for "Delete account" operation.
     */
    private fun initializeDeleteAccountDialog() {
        builderDeleteAccountDialog = AlertDialog.Builder(activity)
            .setTitle(resources.getString(R.string.str_title_delete_account_dialog))
            .setMessage(resources.getString(R.string.str_text_delete_account_dialog))
            .setPositiveButton(resources.getString(R.string.str_title_button_confirm)) { _, _ -> deleteAccount() }
            .setNegativeButton(resources.getString(R.string.str_dialog_button_cancel)) { _, _ ->  }
            .create()
    }

    /**
     * Handles Firebase user account deletion.
     */
    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        context?.let { context ->
            user?.let {
                AuthUI.getInstance().delete(context).addOnSuccessListener {
                    Toast.makeText(context, R.string.toast_account_deleted, Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).apply {
                        finish()
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, R.string.toast_error_delete_account, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    /**
     * Handles interactions with dialog items.
     * @param view: View in which the dialog is attached
     */
    private fun handleCurrencySelectionDialogButtons(view: View?) {
        // EURO currency button
        view?.findViewById<ConstraintLayout>(R.id.constraint_layout_view_euro_currency)
            ?.setOnClickListener { updateCurrency("EUR") }
        // USD currency button
        view?.findViewById<ConstraintLayout>(R.id.constraint_layout_view_usd_currency)
            ?.setOnClickListener { updateCurrency( "USD") }
    }

    /**
     * Saves the selected currency value.
     * @param currency : selection currency
     */
    private fun updateCurrency(currency: String) {
        filePreferences.edit { putString(AppInfo.PREF_CURRENCY, currency) }
        updateDisplayedCurrencyInCardView(currency)
        builderCurrencySelectionDialog.dismiss()
    }

    /**
     * Restores displayed dialog after a configuration change.
     * @param savedInstanceState : Bundle
     */
    private fun restoreDialog(savedInstanceState: Bundle?) {
        if (savedInstanceState?.getBoolean(AppInfo.DIALOG_CURRENCY_SELECT_KEY) == true)
            builderCurrencySelectionDialog.show()
        if (savedInstanceState?.getBoolean(AppInfo.DIALOG_DELETE_ACCOUNT_KEY) == true)
            builderDeleteAccountDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putBoolean(AppInfo.DIALOG_CURRENCY_SELECT_KEY, builderCurrencySelectionDialog.isShowing)
            putBoolean(AppInfo.DIALOG_DELETE_ACCOUNT_KEY, builderDeleteAccountDialog.isShowing)
        }
    }

    /**
     * Updates CardView with the selected currency value.
     * @param selectedCurrency : selected currency
     */
    private fun updateDisplayedCurrencyInCardView(selectedCurrency: String) {
        binding.currencyTextSymbol.text = selectedCurrency
    }

    /**
     * Checks in [DialogsViewModel] if dialogs status before configuration change.
     */
    private fun checkDialogStatusInViewModel() {
        if (dialogsViewModel.deleteAccountDialogStatus) builderDeleteAccountDialog.show()
        if (dialogsViewModel.currencySelectionDialogStatus) builderCurrencySelectionDialog.show()
    }

    override fun onPause() {
        builderDeleteAccountDialog.let { dialogsViewModel.deleteAccountDialogStatus = it.isShowing }
        builderCurrencySelectionDialog.let { dialogsViewModel.currencySelectionDialogStatus = it.isShowing }
        super.onPause()
    }

    override fun onDestroy() {
        builderDeleteAccountDialog.let { if (it.isShowing) it.dismiss() }
        builderCurrencySelectionDialog.let { if (it.isShowing) it.dismiss() }
        super.onDestroy()
    }
}