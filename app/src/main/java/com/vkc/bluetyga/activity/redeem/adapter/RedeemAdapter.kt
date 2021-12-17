package com.vkc.bluetyga.activity.redeem.adapter

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
import com.vkc.bluetyga.activity.cart.model.redeem_history.Data

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
        holder.itemView.setOnClickListener {
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_history)

            val txtDate = dialog.findViewById<View>(R.id.txt_date) as TextView
            val txtType = dialog.findViewById<View>(R.id.txt_type) as TextView
            val txtName = dialog.findViewById<View>(R.id.txt_name) as TextView
            val textPoint = dialog.findViewById<View>(R.id.text_point) as TextView
            val textQuantity = dialog.findViewById<View>(R.id.text_quantity) as TextView
            val textStatus = dialog.findViewById<View>(R.id.text_status) as TextView
            val textDealer = dialog.findViewById<View>(R.id.text_dealer) as TextView
            val buttonCancel = dialog.findViewById<View>(R.id.btn_ok) as Button
            buttonCancel.setOnClickListener {
                dialog.dismiss()
            }
            txtDate.text = ": ${mHistoryData[position].date}"
            txtType.text = ": ${mHistoryData[position].gift_type}"
            txtName.text = ": ${mHistoryData[position].title}"
            textPoint.text = ": ${mHistoryData[position].point}"
            textQuantity.text = ": ${mHistoryData[position].quantity}"
            textStatus.text = ": ${mHistoryData[position].status}"
            textDealer.text = ": ${mHistoryData[position].dealer_name}"
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return  mHistoryData.size
    }
}