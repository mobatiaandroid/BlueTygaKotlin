package com.vkc.bluetyga.activity.dealer_redeem_list.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealer_redeem_list.RedeemReportDetailActivity
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Data
import com.vkc.bluetyga.manager.AppController

class RedeemReportAdapter(context: Activity, redeemReportData: ArrayList<Data>)
    : RecyclerView.Adapter<RedeemReportAdapter.ViewHolder>() {
    var mContext = context
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textCustomerId: TextView? = null
        var textCustomerName: TextView? = null
        var textCustomerPlace: TextView? = null
        var textCustomerMobile: TextView? = null
        var textView: TextView? = null
        init {
            textCustomerId = itemView.findViewById(R.id.txtViewCustId)
            textCustomerName = itemView.findViewById(R.id.txtViewCustName)
            textCustomerPlace = itemView.findViewById(R.id.txtViewPlace)
            textCustomerMobile = itemView.findViewById(R.id.txtViewMobile)
            textView = itemView.findViewById(R.id.txtViewDetail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_redeem_report_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(
                mContext.getColor(
                    R.color.list_row_color_white
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                mContext.getColor(
                    R.color.list_row_color_white
                )
            )

            holder.textCustomerId!!.text = AppController.redeemReportData[position].cust_id
            holder.textCustomerName!!.text = AppController.redeemReportData[position].name
            holder.textCustomerMobile!!.text = AppController.redeemReportData[position].phone
            holder.textCustomerPlace!!.text = AppController.redeemReportData[position].place

            holder.textView!!.setOnClickListener {
                val intent = Intent(mContext, RedeemReportDetailActivity::class.java)
                intent.putExtra("position", position.toString())
                intent.putExtra("cust_id", AppController.redeemReportData[position].cust_id)
                mContext.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.e("siezzz", AppController.redeemReportData.size.toString())
        return AppController.redeemReportData.size
    }
}