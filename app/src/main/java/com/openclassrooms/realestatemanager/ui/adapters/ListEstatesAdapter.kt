package com.openclassrooms.realestatemanager.ui.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Estate

/**
 * Adapter class for [com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate]
 */
class ListEstatesAdapter(private val onItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<ListEstatesAdapter.ListEstateViewHolder>(){

    var listEstates: MutableList<Estate> = mutableListOf()

    inner class ListEstateViewHolder(view: View, val onItemClicked: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        var type: MaterialTextView = view.findViewById(R.id.list_estate_item_text_type)
        var district: MaterialTextView = view.findViewById(R.id.list_estate_item_text_district)
        var price : MaterialTextView = view.findViewById(R.id.list_estate_item_text_price)
        var photo: AppCompatImageView = view.findViewById(R.id.list_estate_item_image)
        var item: ConstraintLayout = view.findViewById(R.id.constraint_layout_item)

        init {
             item.setOnClickListener {
                    onItemClicked(adapterPosition)
             }
        }
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

        holder.district.text = listEstates[position].district

        displayPrice(holder, position)

        displayBackgroundColor(holder, position)
    }

    override fun getItemCount(): Int = listEstates.size

    private fun displayPrice(holder: ListEstateViewHolder, position: Int) {
        val priceFormat: String = formatPrice(listEstates[position].price.toString())
        holder.price.text = priceFormat
        if (listEstates[position].selected)
            holder.price.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    setTextColor(resources.getColor(R.color.white, null))
                else setTextColor(resources.getColor(R.color.white))
            }
        else holder.price.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    setTextColor(resources.getColor(R.color.pink, null))
                else setTextColor(resources.getColor(R.color.pink))
        }
    }

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
     */
    private fun formatPrice(price: String) : String {
        //TODO() : To update to include "€"
        val priceDecimal: Int = (price.toDouble()).toInt()
        return  "$" + String.format("%,d", priceDecimal).replace(" ", ",")
    }

    /**
     * Deselect previous selected item (only one item at a time can be selected)
     */
    fun clearPreviousSelection(position: Int) {
        var found: Boolean = false
        var index: Int = 0
        while ( index < listEstates.size && !found) {
            if (listEstates[index].selected && index != position) {
                listEstates[index].selected = false
                found = true
            }
            else index++
        }
    }

    fun clearCurrentSelection() {
        var found: Boolean = false
        var index: Int = 0
        while ( index < listEstates.size && !found) {
            if (listEstates[index].selected) {
                listEstates[index].selected = false
                found = true
            }
            else index++
        }
        notifyDataSetChanged()
    }

    fun updateItemSelectionStatus(position: Int): Boolean {
        listEstates[position].selected = !listEstates[position].selected
        return listEstates[position].selected
    }
}