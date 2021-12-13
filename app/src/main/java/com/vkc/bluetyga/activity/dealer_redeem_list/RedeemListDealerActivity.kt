package com.vkc.bluetyga.activity.dealer_redeem_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.vkc.bluetyga.R

class RedeemListDealerActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var imageBack: ImageView
    lateinit var imageConsolidate: ImageView
    lateinit var listViewRedeem: ExpandableListView

    private var lastExpandedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_list_dealer)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        imageBack = findViewById(R.id.btn_left)
        imageConsolidate = findViewById(R.id.consolidate)
        listViewRedeem = findViewById(R.id.listViewRedeem)
        listViewRedeem
            .setOnGroupExpandListener { groupPosition ->
                if (lastExpandedPosition != -1
                    && groupPosition != lastExpandedPosition
                ) {
                    listViewRedeem.collapseGroup(lastExpandedPosition)
                }
                lastExpandedPosition = groupPosition
            }
        imageBack.setOnClickListener { finish() }
        imageConsolidate.setOnClickListener {
            startActivity(Intent(this@RedeemListDealerActivity,
                RedeemReportActivity::class.java))
        }
        getRedeemList()
    }

    private fun getRedeemList() {
        TODO("Not yet implemented")
    }
}