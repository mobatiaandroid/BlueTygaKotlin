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

class SubDealerRedeemAdapter(context: Activity)
    : RecyclerView.Adapter<SubDealerRedeemAdapter.ViewHolder>() {
    private var mContext = context
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

            val txtDate = dialog.findViewById<View>(R.id.txt_date) as TextView
            val txtType = dialog.findViewById<View>(R.id.txt_type) as TextView
            val txtName = dialog.findViewById<View>(R.id.txt_name) as TextView
            val textPoint = dialog.findViewById<View>(R.id.text_point) as TextView
            val textQuantity = dialog.findViewById<View>(R.id.text_quantity) as TextView
            val textDealer = dialog.findViewById<View>(R.id.text_dealer) as TextView

            val buttonCancel = dialog.findViewById<View>(R.id.btn_ok) as Button
            txtDate.text = ": ${AppController.redeemHistoryList[position].date}"
            txtType.text = ": ${AppController.redeemHistoryList[position].gift_type}"
            txtName.text = ": ${AppController.redeemHistoryList[position].title}"
            textPoint.text = ": ${AppController.redeemHistoryList[position].point}"
            textQuantity.text = ": ${AppController.redeemHistoryList[position].quantity}"
            textDealer.text = ": ${AppController.redeemHistoryList[position].dealer_name}"
            buttonCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return AppController.redeemHistoryList.size
    }
}