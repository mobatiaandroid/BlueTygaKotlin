package com.vkc.bluetyga.activity.point_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.manager.AppController

class TransactionHistoryAdapter :
    BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return AppController.transactionData.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return AppController.transactionDetails.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return AppController.transactionData[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return AppController.transactionData[groupPosition].details[childPosition]
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
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_history_parent, null)
        }
        val textUser = view!!.findViewById<View>(R.id.textUser) as TextView
        val textPoint = view.findViewById<View>(R.id.textPoint) as TextView

        val textIcon = view.findViewById<View>(R.id.textIcon) as TextView
        textUser.text = AppController.transactionData[groupPosition].to_name
        val points = AppController.transactionData[groupPosition].tot_points
//        textPoint.text = parent!!.context.getString(R.string.total_coupons, points)
        textPoint.text = "$points Coupons"
        if (isExpanded) {
            textIcon.text = "-"
        } else {
            textIcon.text = "+"
        }
        return view
    }
    internal class ViewHolder {
        var textType: TextView? = null
        var textPoints: TextView? = null
        var textToUser: TextView? = null
        var textDate: TextView? = null
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
            currentChildView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_history_child, null)
            viewHolder = ViewHolder()


            currentChildView.tag = viewHolder
        }
        viewHolder =
            currentChildView!!.tag as ViewHolder
        viewHolder.textType = currentChildView.findViewById<View>(R.id.textType) as TextView
        viewHolder.textPoints = currentChildView.findViewById<View>(R.id.textPoints) as TextView
        viewHolder.textToUser = currentChildView.findViewById<View>(R.id.textToUser) as TextView
        viewHolder.textDate = currentChildView.findViewById<View>(R.id.textDate) as TextView


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

        viewHolder.textType!!.text = AppController.transactionDetails[childPosition].type
        viewHolder.textPoints!!.text = AppController.transactionDetails[childPosition].points
        if (AppController.transactionDetails[childPosition].to_name.isNotEmpty()) {
            viewHolder.textToUser!!.text = (AppController.transactionDetails[childPosition]
                .to_name + " / " + AppController.transactionDetails[childPosition].to_role)
        } else {
            viewHolder.textToUser!!.text = ""
        }

        viewHolder.textDate!!.text = AppController.transactionDetails[childPosition].date
        return currentChildView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }
}