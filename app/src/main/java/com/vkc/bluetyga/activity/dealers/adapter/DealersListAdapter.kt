package com.vkc.bluetyga.activity.dealers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealers.model.get_dealers.Data
import com.vkc.bluetyga.manager.AppController

class DealersListAdapter(var context: Context, var dealersList: ArrayList<Data>)
    : RecyclerView.Adapter<DealersListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var textName: TextView? = null
        var checkBox: CheckBox? = null

        init {
            textName = itemView.findViewById(R.id.textViewName)
            checkBox = itemView.findViewById(R.id.checkbox_dealer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dealers_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textName!!.text = AppController.dealersList[position].name
        holder.checkBox!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                AppController.dealersList[position].is_assigned = "1"
            }else{
                AppController.dealersList[position].is_assigned = "0"
            }
        }
        holder.checkBox!!.isChecked = dealersList[position].is_assigned == "1"
    }

    override fun getItemCount(): Int {
        return AppController.dealersList.size
    }
}
