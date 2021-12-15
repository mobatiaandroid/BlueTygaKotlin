package com.vkc.bluetyga.activity.dealer_redeem_list

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealer_redeem_list.adapter.ReportDetailAdapter
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager

class RedeemReportDetailActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var recyclerRedeemReportDetailList: RecyclerView
    lateinit var headerManager: HeaderManager
    lateinit var imageBack: ImageView
    var customerID: String? = null
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_report_detail)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        recyclerRedeemReportDetailList = findViewById(R.id.recyclerRedeemReportDetailList)
        recyclerRedeemReportDetailList.hasFixedSize()
        recyclerRedeemReportDetailList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        headerManager = HeaderManager(
            this@RedeemReportDetailActivity,
            resources.getString(R.string.redeem_report_detail)
        )
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        imageBack.setOnClickListener { finish() }
        val intent = intent
        //position = Integer.parseInt(intent.getExtras().getString("position"));
        //position = Integer.parseInt(intent.getExtras().getString("position"));
        customerID = intent.extras!!.getString("cust_id")
        for (i in 0 until AppController.redeemReportData.size) {
            if (AppController.redeemReportData[i].cust_id == customerID
            ) {
                position = i
                break
            }
        }
        val adapter = ReportDetailAdapter(
            context,
            AppController.redeemReportData[position]
                .details
        )
        recyclerRedeemReportDetailList.adapter = adapter
    }
}