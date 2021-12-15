package com.vkc.bluetyga.activity.customers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.manager.AppController


class CustomersListAdapter(var context: Context)
    : RecyclerView.Adapter<CustomersListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var textName: TextView? = null
        var textRole: TextView? = null
        var textPhone: TextView? = null

        init {
            textName = itemView.findViewById(R.id.textName)
            textRole = itemView.findViewById(R.id.textRole)
            textPhone = itemView.findViewById(R.id.textPhone)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customers_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.list_row_color_grey))
        } else {
            holder.itemView.setBackgroundColor(context.getColor(R.color.list_row_color_white))
        }
        holder.textName!!.text = AppController.customersList[position].name
        holder.textPhone!!.text = AppController.customersList[position].phone
        holder.textRole!!.text = AppController.customersList[position].role
    }

    override fun getItemCount(): Int {
        return AppController.customersList.size
    }
}


