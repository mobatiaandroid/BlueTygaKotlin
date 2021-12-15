package com.vkc.bluetyga.activity.inbox.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.inbox.InboxDetailsActivity
import com.vkc.bluetyga.activity.inbox.model.inbox.Data

class InboxAdapter(var context: Context, var notificationList: ArrayList<Data>)
    : RecyclerView.Adapter<InboxAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageTile: ImageView? = null
        var textTitle: TextView? = null
        var textDate: TextView? = null
        var linearLayout: LinearLayout? = null
        init {
            textTitle = itemView.findViewById<View>(R.id.textTitle) as TextView
            textDate = itemView.findViewById<View>(R.id.textDate) as TextView
            imageTile = itemView.findViewById<View>(R.id.image_inbox) as ImageView
            linearLayout = itemView.findViewById<View>(R.id.thumbnail) as LinearLayout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inbox_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textTitle!!.text = (notificationList[position].title)
        holder.textDate!!.text = (notificationList[position].createdon)

        if (notificationList[position].image == "") {
            holder.imageTile!!.visibility = View.INVISIBLE
        } else {
            holder.imageTile!!.visibility = View.VISIBLE
            notificationList[position].image
            Glide.with(context).load(notificationList[position].image).into(holder.imageTile!!)
        }
        holder.itemView.setOnClickListener {
            try{
                val intent = Intent(context, InboxDetailsActivity::class.java)
                intent.putExtra("position", position)
                context.startActivity(intent)
            }catch(e:Exception){
                Log.e("Error", e.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}