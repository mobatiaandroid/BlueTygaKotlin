package com.vkc.bluetyga.activity.dealer_redeem_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealer_redeem_list.adapter.RedeemListAdapter
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.Data
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.Detail
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.RedeemedGiftsMainResponseModel
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.Response
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class RedeemListDealerActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var imageBack: ImageView
    lateinit var imageConsolidate: ImageView
    lateinit var listViewRedeem: ExpandableListView
    lateinit var progressBarDialog: ProgressBarDialog
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
        progressBarDialog = ProgressBarDialog(context)
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
        var redeemHistoryMainResponse: RedeemedGiftsMainResponseModel
        var redeemHistoryResponse: Response
        var redeemHistoryData: ArrayList<Data> = ArrayList()
        var redeemHistoryDetails: ArrayList<Detail> = ArrayList()
        AppController.redeemHistoryDataDealer.clear()
        AppController.redeemHistoryDetailDealer.clear()
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getRedeemHistoryForDealer(
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<RedeemedGiftsMainResponseModel>{
                override fun onResponse(
                    call: Call<RedeemedGiftsMainResponseModel>,
                    response: retrofit2.Response<RedeemedGiftsMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        redeemHistoryMainResponse = response.body()!!
                        redeemHistoryResponse = redeemHistoryMainResponse.response
                        if (redeemHistoryResponse.status == "Success"){
                            if (redeemHistoryResponse.data.isNotEmpty()){
                                for ( i in redeemHistoryResponse.data.indices){
                                    for (j in redeemHistoryResponse.data[i].details.indices){
                                        AppController.redeemHistoryDetailDealer.add(redeemHistoryResponse.data[i].details[j])
                                    }
                                    AppController.redeemHistoryDataDealer.add(redeemHistoryResponse.data[i])
                                }
                                val adapter = RedeemListAdapter()
                                listViewRedeem.setAdapter(adapter)
                            }else{

                            }
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(24)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<RedeemedGiftsMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(0)
                }
            })
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }

    }
}