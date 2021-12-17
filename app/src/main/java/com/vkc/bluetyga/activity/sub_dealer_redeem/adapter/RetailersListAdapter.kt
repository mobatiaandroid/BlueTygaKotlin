package com.vkc.bluetyga.activity.sub_dealer_redeem.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.customers.adapter.CustomersListAdapter
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.sub_dealer_retailer.Data
import com.vkc.bluetyga.manager.AppController

class RetailersListAdapter(context: Activity, retailerData: ArrayList<Data>)
    :RecyclerView.Adapter<RetailersListAdapter.ViewHolder>(){
    var mContext = context
    var mRetailerData = retailerData
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textName: TextView? = null
        var checkBox: CheckBox? = null
        init {
            textName = itemView.findViewById<View>(R.id.textViewName) as TextView?
            checkBox = itemView.findViewById<View>(R.id.checkbox_dealer) as CheckBox?
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dealer_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox!!
            .setOnCheckedChangeListener { buttonView, isChecked ->
                val getPosition = buttonView.tag as Int
                AppController.retailerData[getPosition].isChecked = (isChecked)
            }
        holder.checkBox!!.tag = position
        holder.textName!!.text = AppController.retailerData[position]
            .user_name

        holder.checkBox!!.isChecked = AppController.retailerData[position].isChecked
    }

    override fun getItemCount(): Int {
        return AppController.retailerData.size
    }
}