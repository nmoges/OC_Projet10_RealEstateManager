package com.openclassrooms.realestatemanager.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.data.model.Estate
import com.openclassrooms.realestatemanager.AppInfo
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.ui.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.ProgressBarHandler

/**
 * Adapter class for [com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate]
 */
class ListEstatesAdapter(private val context: Context, private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<ListEstatesAdapter.ListEstateViewHolder>(){

    /** Contains list of [Estate] to display in recycler view */
    var listEstates: MutableList<Estate> = mutableListOf()

    /** Contains the currency to display */
    private var currency: String = "USD"

    /** Parent activity */
    lateinit var activity: MainActivity

    /**
     * Defines a custom [RecyclerView.ViewHolder].
     * @param view : view
     * @param onItemClicked : lambda function handling clicks on list item
     */
    inner class ListEstateViewHolder(view: View, val onItemClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        var type: MaterialTextView = view.findViewById(R.id.list_estate_item_text_type)
        var district: MaterialTextView = view.findViewById(R.id.list_estate_item_text_district)
        var price : MaterialTextView = view.findViewById(R.id.list_estate_item_text_price)
        var photo: ImageView = view.findViewById(R.id.list_estate_item_image)
        var item: ConstraintLayout = view.findViewById(R.id.constraint_layout_item)
        var status: MaterialTextView = view.findViewById(R.id.status)

        init { item.setOnClickListener { onItemClicked(adapterPosition) } }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEstateViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                                       .inflate(R.layout.fragment_list_estate_item,
                                                parent,
                                     false)
        return ListEstateViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ListEstateViewHolder, position: Int) {
        holder.type.text = listEstates[position].type
        holder.district.text = listEstates[position].location.district
        displayPrice(holder, position)
        displayBackgroundColor(holder, position)
        displayPhoto(holder, position)
        displaySaleStatus(holder, position)
    }

    override fun getItemCount(): Int = listEstates.size

    /**
     * Handles price display for each recycler view item.
     * @param holder : view holder
     * @param position : position in [listEstates] list
     */
    private fun displayPrice(holder: ListEstateViewHolder, position: Int) {
        // Handle conversion USD -> EUR if needed
        val price: Int = if (currency == "USD") listEstates[position].price
                         else Utils.convertDollarToEuro(listEstates[position].price)
        // Display price
        val priceFormat: String = formatPrice(price.toString())
        holder.price.text = priceFormat
        // Handle style
        if (listEstates[position].selected)
            holder.price.apply {
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        else holder.price.apply {
                setTextColor(ContextCompat.getColor(context, R.color.pink))
            }
    }

    /**
     * Handles photo display for each recycler view item.
     * @param holder : view holder
     * @param position : position in [listEstates] list
     */
    private fun displayPhoto(holder: ListEstateViewHolder, position: Int) {
        if (listEstates[position].listPhoto.size > 0) {
            Glide.with(activity)
                 .load((listEstates[position].listPhoto[0].uriConverted).toUri())
                 .centerCrop()
                 .placeholder(ProgressBarHandler.getProgressBarDrawable(context))
                 .override(holder.photo.width, holder.photo.height)
                 .into(holder.photo)
        }
    }

    /**
     * Handles background color display for each recycler view item.
     * @param holder : view holder
     * @param position : position in [listEstates] list
     */
    @Suppress("DEPRECATION")
    private fun displayBackgroundColor(holder: ListEstateViewHolder, position: Int) {
        if (listEstates[position].selected)
            holder.item.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    setBackgroundColor(resources.getColor(R.color.pink, null))
                else setBackgroundColor(resources.getColor(R.color.pink))
            }
        else holder.item.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    setBackgroundColor(resources.getColor(R.color.white, null))
                else setBackgroundColor(resources.getColor(R.color.white))
        }
    }

    /**
     * Convert price to correct format before displaying on RecyclerView.
     * @param price : Price in String format
     */
    private fun formatPrice(price: String) : String {
        val priceDecimal: Int = (price.toDouble()).toInt()
        val symbol: String = if (currency == "USD") "$" else "???"
        return symbol + String.format("%,d", priceDecimal).replace(" ", ",")
    }

    /**
     * Deselect previous selected item (only one item at a time can be selected)
     * @param position : position in listEstates
     */
    fun clearPreviousSelection(position: Int) {
        var found = false
        var index = 0
        while ( index < listEstates.size && !found) {
            if (listEstates[index].selected && index != position) {
                listEstates[index].selected = false
                found = true
            }
            else index++
        }
    }

    /**
     * Clears current selected item.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clearCurrentSelection() {
        var found = false
        var index = 0
        while ( index < listEstates.size && !found) {
            if (listEstates[index].selected) {
                listEstates[index].selected = false
                found = true
            }
            else index++
        }
        notifyDataSetChanged()
    }

    /**
     * Updated "selection" status of a selected item in list.
     * @param position : position in list
     */
    fun updateItemSelectionStatus(position: Int): Boolean {
        listEstates[position].selected = !listEstates[position].selected
        return listEstates[position].selected
    }

    /**
     * Handles visibility value of the "sale status" displayed.
     * @param holder : view holder
     * @param position : item position in the list of estates
     */
    private fun displaySaleStatus(holder: ListEstateViewHolder, position: Int) {
        holder.status.visibility = if (listEstates[position].status) View.VISIBLE
                                   else View.INVISIBLE
    }

    /**
     * Updates [currency] with saved value in SharedPreferences file.
     */
    fun updateCurrencyWithSharedPreferencesValue() {
        context.getSharedPreferences(AppInfo.FILE_SHARED_PREF, Context.MODE_PRIVATE).apply {
            this.getString(AppInfo.PREF_CURRENCY, "USD")?.let {
                currency = it
            }
        }
    }
}