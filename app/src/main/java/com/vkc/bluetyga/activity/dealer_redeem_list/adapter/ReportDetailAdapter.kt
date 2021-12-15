package com.vkc.bluetyga.activity.dealer_redeem_list.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Detail

class ReportDetailAdapter(context: Activity, details: ArrayList<Detail>)
    : RecyclerView.Adapter<ReportDetailAdapter.ViewHolder>(){
    var mContext = context
    var detailsList = details
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var textGiftName: TextView? = null
        var textGiftQty: TextView? = null
        var textRewardPoints: TextView? = null
        var textTotalCoupons: TextView? = null
        init {
            textGiftName = itemView.findViewById(R.id.txtViewGift)
            textGiftQty = itemView.findViewById(R.id.txtViewGiftQty)
            textRewardPoints = itemView.findViewById(R.id.txtViewRedeemPoints)
            textTotalCoupons = itemView.findViewById(R.id.txtViewTotalCoupons)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_detail, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 1) {
            // view.setBackgroundColor(Color.BLUE);
            holder.itemView.setBackgroundColor(
                mContext.getColor(
                    R.color.list_row_color_grey
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                mContext.getColor(
                    R.color.list_row_color_white
                )
            )
        }

        holder.textGiftName!!.text = detailsList[position].gift_name
        holder.textGiftQty!!.text = detailsList[position].gift_qty
        holder.textRewardPoints!!.text = (detailsList[position].rwd_points).toString()
        holder.textTotalCoupons!!.text = ((detailsList[position].tot_coupons).toString()
        )
    }

    override fun getItemCount(): Int {
        return detailsList.size
    }
}