package com.vkc.bluetyga.activity.redeem

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.cart.model.redeem_history.Data
import com.vkc.bluetyga.activity.cart.model.redeem_history.RedeemHistoryMainModel
import com.vkc.bluetyga.activity.cart.model.redeem_history.Response
import com.vkc.bluetyga.activity.redeem.adapter.RedeemAdapter
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class RedeemHistoryActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var imageBack: ImageView
    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var recyclerRedeem: RecyclerView
    var historyData: ArrayList<Data> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_history)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        imageBack = findViewById(R.id.btn_left)
        progressBarDialog = ProgressBarDialog(context)
        imageBack.setOnClickListener {
            finish()
        }
        recyclerRedeem = findViewById(R.id.recyclerRedeem)
        recyclerRedeem.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        getHistory()
    }

    private fun getHistory() {
        var mainResponse: RedeemHistoryMainModel
        var historyResponse: Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getRedeemHistory(
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<RedeemHistoryMainModel>{
                override fun onResponse(
                    call: Call<RedeemHistoryMainModel>,
                    response: retrofit2.Response<RedeemHistoryMainModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        historyResponse = mainResponse.response
                        if (historyResponse.status.equals("Success")){
                            if (historyResponse.data.isNotEmpty()){
                                historyData = historyResponse.data
                                val adapter = RedeemAdapter(context, historyData)
                                recyclerRedeem.adapter = adapter
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(4)
                            }
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(4)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<RedeemHistoryMainModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(context)
                    CustomToast.show(58)
                }
            })
        }else{
            CustomToast.customToast(context)
            CustomToast.show(58)
        }
    }
}