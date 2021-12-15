package com.vkc.bluetyga.activity.dealer_redeem_list

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealer_redeem_list.adapter.RedeemReportAdapter
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Data
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Detail
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.RedeemReportMainResponseModel
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Response
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class RedeemReportActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var headerManager: HeaderManager
    lateinit var recyclerViewRedeemReport: RecyclerView
    lateinit var imageBack: ImageView
    lateinit var editSearch: EditText
    lateinit var progressBarDialog: ProgressBarDialog
    var tempRedeemReportList: ArrayList<Data> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeem_report)
        context = this
        initialiseUI()
        getReport()
    }



    private fun initialiseUI() {
        header = findViewById(R.id.header)
        editSearch = findViewById(R.id.editSearch)
        progressBarDialog = ProgressBarDialog(context)
        recyclerViewRedeemReport = findViewById(R.id.recyclerRedeemReportList)
        recyclerViewRedeemReport.hasFixedSize()
        recyclerViewRedeemReport.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        headerManager = HeaderManager(this@RedeemReportActivity,
            resources.getString(R.string.redeem_report))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        imageBack.setOnClickListener { finish() }
        editSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    tempRedeemReportList.clear()
                    for (i in 0 until AppController.redeemReportData.size) {
                        if (AppController.redeemReportData[i].cust_id
                                .contains(s)
                            || AppController.redeemReportData[i]
                                .cust_id.lowercase(Locale.getDefault()).contains(s)
                            || AppController.redeemReportData[i]
                                .phone.contains(s)
                            || AppController.redeemReportData[i]
                                .place.lowercase(Locale.getDefault()).contains(s)
                        ) {
                            tempRedeemReportList
                                .add(AppController.redeemReportData[i])
                        } else {
                            recyclerViewRedeemReport.adapter = null
                        }
                    }
                     val adapter = RedeemReportAdapter(
                         context
                     )
                    adapter.notifyDataSetChanged()
                    recyclerViewRedeemReport.adapter = adapter
                } else {
                    val adapter = RedeemReportAdapter(
                        context
                    )
                    adapter.notifyDataSetChanged()
                    recyclerViewRedeemReport.adapter = adapter
                }
            }

            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
            }
        })
    }
    private fun getReport() {
        var redeemReportMainResponse: RedeemReportMainResponseModel
        var redeemReportResponse: Response
        var redeemReportData: ArrayList<Data> = ArrayList()
        var redeemDetails: ArrayList<Detail> = ArrayList()
        AppController.redeemReportData.clear()
        AppController.redeemReportDetail.clear()
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getRedeemReportResponse(
//                PreferenceManager.getCustomerID(context)
            "2"
            ).enqueue(object : Callback<RedeemReportMainResponseModel> {
                override fun onResponse(
                    call: Call<RedeemReportMainResponseModel>,
                    response: retrofit2.Response<RedeemReportMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    redeemReportMainResponse = response.body()!!
                    redeemReportResponse = redeemReportMainResponse.response
                    if (redeemReportResponse.status == "Success"){
                        if (redeemReportResponse.data.isNotEmpty()){
                            for (i in redeemReportResponse.data.indices){
                                Log.e("RedeemReport",redeemReportResponse.toString())
                                for (j in redeemReportResponse.data[i].details.indices){
                                    AppController.redeemReportDetail.add(redeemReportResponse.data[i].details[j])
                                }
                                AppController.redeemReportData.add(redeemReportResponse.data[i])
                            }
                            Log.e("size", AppController.redeemReportData.toString())
                            val adapter = RedeemReportAdapter(
                                context
                            )
                            recyclerViewRedeemReport.adapter = adapter
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else if (redeemReportResponse.status == "scheme_error"){
                        CustomToast.customToast(context)
                        CustomToast.show(68)
                    }
                }

                override fun onFailure(call: Call<RedeemReportMainResponseModel>, t: Throwable) {
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