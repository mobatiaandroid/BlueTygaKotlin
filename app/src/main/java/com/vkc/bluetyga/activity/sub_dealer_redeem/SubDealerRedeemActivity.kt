package com.vkc.bluetyga.activity.sub_dealer_redeem

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.sub_dealer_redeem.adapter.SubDealerRedeemAdapter
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.redeem_history.Data
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.redeem_history.Response
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.redeem_history.SubDealerRedeemHistoryMainResponse
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class SubDealerRedeemActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var recyclerRedeemList: RecyclerView
    lateinit var headerManager: HeaderManager
    lateinit var imageBack: ImageView
    lateinit var progressBarDialog: ProgressBarDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_dealer_redeem)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        recyclerRedeemList = findViewById(R.id.recyclerRedeemList)
        headerManager =
            HeaderManager(this@SubDealerRedeemActivity, "Redeem History")
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        imageBack.setOnClickListener { finish() }
//        redeemHistory = ArrayList()

        getHistory()
    }

    private fun getHistory() {
        var redeemHistoryMainResponse: SubDealerRedeemHistoryMainResponse
        var redeemHistoryResponse: Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getRedeemHistoryForSubDealer(
                PreferenceManager.getCustomerID(context)
            ).enqueue(object : Callback<SubDealerRedeemHistoryMainResponse>{
                override fun onResponse(
                    call: Call<SubDealerRedeemHistoryMainResponse>,
                    response: retrofit2.Response<SubDealerRedeemHistoryMainResponse>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        redeemHistoryMainResponse = response.body()!!
                        redeemHistoryResponse = redeemHistoryMainResponse.response
                        if (redeemHistoryResponse.status == "Success"){
                            AppController.redeemHistoryList = redeemHistoryResponse.data
                            val adapter = SubDealerRedeemAdapter(context)
                            recyclerRedeemList.adapter = adapter
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(
                    call: Call<SubDealerRedeemHistoryMainResponse>,
                    t: Throwable
                ) {
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