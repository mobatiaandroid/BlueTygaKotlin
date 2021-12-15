package com.vkc.bluetyga.activity.dealer_redeem_list.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.customers.adapter.CustomersListAdapter
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.Detail
import com.vkc.bluetyga.activity.point_history.adapter.TransactionHistoryAdapter
import com.vkc.bluetyga.manager.AppController

class RedeemListAdapter(context: Activity, redeemHistoryDetailDealer: ArrayList<Detail>)
    : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return AppController.redeemHistoryDataDealer.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return AppController.redeemHistoryDetailDealer.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return AppController.redeemHistoryDataDealer[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return AppController.redeemHistoryDataDealer[groupPosition].details[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_history_parent, null)
        }
        val textUser = convertView!!.findViewById<View>(R.id.textUser) as TextView
        val textPoints = convertView
            .findViewById<View>(R.id.textPoint) as TextView
        val textIcon = convertView.findViewById<View>(R.id.textIcon) as TextView
        textPoints.text = "Mobile :${AppController.redeemHistoryDataDealer[groupPosition].phone}"
        textUser.text = AppController.redeemHistoryDataDealer[groupPosition].name
        if (isExpanded) {
            textIcon.text = "-"
        } else {
            textIcon.text = "+"
        }
        return convertView
    }
    internal class ViewHolder {
        var textGiftType: TextView? = null
        var textGiftQuantity: TextView? = null
        var textGiftName: TextView? = null
    }
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var currentChildView = convertView
        var viewHolder: ViewHolder
        if (currentChildView == null) {
            currentChildView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_history_redeem_child, null)
            viewHolder = ViewHolder()
            currentChildView.tag = viewHolder
        }
        viewHolder = currentChildView!!.tag as ViewHolder
        viewHolder.textGiftName = currentChildView.findViewById<View>(R.id.textGiftName) as TextView?
        viewHolder.textGiftType = currentChildView.findViewById<View>(R.id.textGiftType) as TextView?
        viewHolder.textGiftQuantity = currentChildView.findViewById<View>(R.id.textGiftQuantity) as TextView?

        // viewHolder.imageGift = (ImageView) v.findViewById(R.id.imageGift);

        // childPosition=childPosition-1;
        if (childPosition % 2 == 1) {
            currentChildView.setBackgroundColor(
                parent!!.context.getColor(
                    R.color.list_row_color_grey
                )
            )
        } else {
            currentChildView.setBackgroundColor(
                parent!!.context.getColor(
                    R.color.list_row_color_white
                )
            )
        }
        // String date=productList.get(childPosition).getDateValue();
        viewHolder.textGiftName!!.text = AppController.redeemHistoryDetailDealer[childPosition].gift_title
        viewHolder.textGiftType!!.text = AppController.redeemHistoryDetailDealer[childPosition].gift_type
        viewHolder.textGiftQuantity!!.text = AppController.redeemHistoryDetailDealer[childPosition].quantity
        return currentChildView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }
}