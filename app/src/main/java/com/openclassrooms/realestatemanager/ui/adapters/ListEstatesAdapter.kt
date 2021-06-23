package com.openclassrooms.realestatemanager.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Estate

/**
 * Adapter class for [com.openclassrooms.realestatemanager.ui.fragments.FragmentListEstate]
 */
class ListEstatesAdapter : RecyclerView.Adapter<ListEstatesAdapter.ListEstateViewHolder>(){

    var listEstates: MutableList<Estate> = mutableListOf()
    var onItemClickListener: ((Estate) -> Unit)?  = null

    inner class ListEstateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var type: MaterialTextView = view.findViewById(R.id.list_estate_item_text_type)
        var district: MaterialTextView = view.findViewById(R.id.list_estate_item_text_district)
        var price : MaterialTextView = view.findViewById(R.id.list_estate_item_text_price)
        var photo: AppCompatImageView = view.findViewById(R.id.list_estate_item_image)

        init {
            view.setOnClickListener {
                onItemClickListener?.invoke(listEstates[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEstateViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                                       .inflate(R.layout.fragment_list_estate_item,
                                                parent,
                                     false)
        return ListEstateViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListEstateViewHolder, position: Int) {
        holder.type.text = listEstates[position].type

        holder.district.text = listEstates[position].district

        holder.price.text = listEstates[position].price.toBigDecimal().toPlainString()
    }

    override fun getItemCount(): Int = listEstates.size

  /*  interface OnItemClickListener {
        fun onClick(position: Int)
    }*/
}