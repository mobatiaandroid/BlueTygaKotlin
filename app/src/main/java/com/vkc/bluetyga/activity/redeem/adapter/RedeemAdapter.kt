package com.vkc.bluetyga.activity.redeem.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.cart.model.redeem_history.Data
import com.vkc.bluetyga.activity.customers.adapter.CustomersListAdapter

class RedeemAdapter(context: Activity, historyData: ArrayList<Data>):
    RecyclerView.Adapter<RedeemAdapter.ViewHolder>() {
    var mContext = context
    var mHistoryData = historyData
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textDate: TextView? = null
        var textType: TextView? = null
        var textTitle: TextView? = null
        var textStatus: TextView? = null
        init {
            textDate = itemView.findViewById<View>(R.id.textDate) as TextView?
            textType = itemView.findViewById<View>(R.id.textType) as TextView?
            textTitle = itemView.findViewById<View>(R.id.textTitle) as TextView?
            textStatus = itemView.findViewById<View>(R.id.textStatus) as TextView?

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_redeem_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textDate!!.text = mHistoryData[position].date
        holder.textType!!.text = mHistoryData[position].gift_type
        holder.textTitle!!.text = mHistoryData[position].title
        holder.textStatus!!.text = mHistoryData[position].status
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}