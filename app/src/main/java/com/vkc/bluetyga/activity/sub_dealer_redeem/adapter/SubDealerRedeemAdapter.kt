package com.vkc.bluetyga.activity.sub_dealer_redeem.adapter

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.manager.AppController

class SubDealerRedeemAdapter(context: Activity,
                             redeemHistory: ArrayList<com.vkc.bluetyga.activity.sub_dealer_redeem.model.redeem_history.Data>)
    : RecyclerView.Adapter<SubDealerRedeemAdapter.ViewHolder>() {
    var mContext = context
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textRetailer: TextView? = null
        var textType: TextView? = null
        var textTitle: TextView? = null
        var textStatus: TextView? = null
        init {
            textRetailer = itemView.findViewById(R.id.textRetailer)
            textTitle = itemView.findViewById(R.id.textTitle)
            textType = itemView.findViewById(R.id.textType)
            textStatus = itemView.findViewById(R.id.textStatus)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_redeem_history_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textRetailer!!.text = AppController.redeemHistoryList[position].retailer_name
        holder.textType!!.text = AppController.redeemHistoryList[position].title
        holder.textTitle!!.text = AppController.redeemHistoryList[position].point
        holder.textStatus!!.text = AppController.redeemHistoryList[position].status
        holder.itemView.setOnClickListener {
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_subdealer_history)

            val txt_date = dialog.findViewById<View>(R.id.txt_date) as TextView
            val txt_type = dialog.findViewById<View>(R.id.txt_type) as TextView
            val txt_name = dialog.findViewById<View>(R.id.txt_name) as TextView
            val text_point = dialog.findViewById<View>(R.id.text_point) as TextView
            val text_quantity = dialog.findViewById<View>(R.id.text_quantity) as TextView
            val text_dealer = dialog.findViewById<View>(R.id.text_dealer) as TextView

            val buttonCancel = dialog.findViewById<View>(R.id.btn_ok) as Button
            txt_date.text = ": ${AppController.redeemHistoryList[position].date}"
            txt_type.text = ": ${AppController.redeemHistoryList[position].gift_type}"
            txt_name.text = ": ${AppController.redeemHistoryList[position].title}"
            text_point.text = ": ${AppController.redeemHistoryList[position].point}"
            text_quantity.text = ": ${AppController.redeemHistoryList[position].quantity}"
            text_dealer.text = ": ${AppController.redeemHistoryList[position].dealer_name}"
            buttonCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return AppController.redeemHistoryList.size
    }
}