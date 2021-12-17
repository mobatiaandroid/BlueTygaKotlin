package com.vkc.bluetyga.activity.sub_dealer_redeem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.Data
import com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.DealerSubDealerMainResponseModel
import com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.Response
import com.vkc.bluetyga.activity.sub_dealer_redeem.adapter.RetailersListAdapter
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.place_order_sub_dealer.OrderSubDealerResponse
import com.vkc.bluetyga.activity.sub_dealer_redeem.model.sub_dealer_retailer.SubDealerRetailerMainResponseModel
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class SubDealerRedeemActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var recyclerList: RecyclerView
    lateinit var imageBack: ImageView
    lateinit var imageHistory: ImageView
    lateinit var spinnerDealer: Spinner
    lateinit var buttonOrder: Button
    lateinit var progressBarDialog: ProgressBarDialog
    var dealerData: ArrayList<Data> = ArrayList()
    var dealerID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_dealer_redeem2)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        recyclerList = findViewById(R.id.recyclerRedeemList)
        recyclerList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        imageBack = findViewById(R.id.btn_left)
        imageHistory = findViewById(R.id.btn_right)
        spinnerDealer = findViewById(R.id.spinnerDealer)
        buttonOrder = findViewById(R.id.buttonOrder)
        progressBarDialog = ProgressBarDialog(context)
        imageBack.setOnClickListener {
            finish()
        }
        imageHistory.setOnClickListener {
            startActivity(
                Intent(
                    this@SubDealerRedeemActivity,
                    SubDealerRedeemHistoryActivity::class.java
                )
            )
        }
        buttonOrder.setOnClickListener {
            if (dealerID == "") {
                CustomToast.customToast(context)
                CustomToast.show(45)
            } else if (AppController.retailerData.size == 0) {
                CustomToast.customToast(context)
                CustomToast.show(46)
            }

            //  else {


            //  else {
            val listRetailer = java.util.ArrayList<String>()
            for (i in AppController.retailerData.indices) {
                if (AppController.retailerData[i].isChecked) {
                    listRetailer.add(AppController.retailerData[i].retailer_id)
                }
            }
            if (listRetailer.size == 0) {
                CustomToast.customToast(context)
                CustomToast.show(65)
            } else if (listRetailer.size > 0 && dealerID != "") {
                val gson = GsonBuilder().create()
                val details = gson.toJsonTree(listRetailer).asJsonArray
                placeOrder(details.toString())
            }
        }
        spinnerDealer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).gravity = Gravity.CENTER
                (parent.getChildAt(0) as TextView).textSize = 12f
                if (position > 0) {
                    dealerID = dealerData[position - 1].id
                } else {
                    dealerID = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        getDealers()
    }

    private fun placeOrder(json: String) {
        var mainResponse: OrderSubDealerResponse
        var orderResponse: com.vkc.bluetyga.activity.sub_dealer_redeem.model.place_order_sub_dealer.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().placeOrderSubDealer(
                json,
                PreferenceManager.getCustomerID(context),
                dealerID
            ).enqueue( object : Callback<OrderSubDealerResponse>{
                override fun onResponse(
                    call: Call<OrderSubDealerResponse>,
                    response: retrofit2.Response<OrderSubDealerResponse>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        orderResponse = mainResponse.response
                        if (orderResponse.status == "Success"){
                            CustomToast.customToast(context)
                            CustomToast.show(47)
                            spinnerDealer.setSelection(0)
                            dealerID = ""
                            getRetailerList()
                        }else if (orderResponse.status.equals("scheme_error",ignoreCase = true)){
                            CustomToast.customToast(context)
                            CustomToast.show(64)
                            spinnerDealer.setSelection(0)
                            dealerID = ""
                            getRetailerList()
                        }else{

                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<OrderSubDealerResponse>, t: Throwable) {
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

    private fun getDealers() {
        var mainResponse: DealerSubDealerMainResponseModel
        var dealerResponse: Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getDealerAndSubDealersResponse(
                PreferenceManager.getCustomerID(context),
                "7"
            ).enqueue( object : Callback<DealerSubDealerMainResponseModel>{
                override fun onResponse(
                    call: Call<DealerSubDealerMainResponseModel>,
                    response: retrofit2.Response<DealerSubDealerMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        dealerResponse = mainResponse.response
                        if (dealerResponse.status == "Success"){
                            if (dealerResponse.data.size > 0){
                                dealerData = dealerResponse.data
                                val listDealer = java.util.ArrayList<String>()
                                listDealer.add("Select Dealer")
                                for (i in dealerData.indices) {
                                    listDealer.add(dealerData[i].name)
                                }
                                val adapter = ArrayAdapter(
                                    this@SubDealerRedeemActivity,
                                    android.R.layout.simple_spinner_item,
                                    listDealer
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerDealer.adapter = adapter
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(44)
                            }
                            getRetailerList()
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(4)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<DealerSubDealerMainResponseModel>, t: Throwable) {
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

    private fun getRetailerList() {
        var mainResponse: SubDealerRetailerMainResponseModel
        var subDealerResponse: com.vkc.bluetyga.activity.sub_dealer_redeem.model.sub_dealer_retailer.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getSubDealerAndRetailer(
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<SubDealerRetailerMainResponseModel>{
                override fun onResponse(
                    call: Call<SubDealerRetailerMainResponseModel>,
                    response: retrofit2.Response<SubDealerRetailerMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        subDealerResponse = mainResponse.response
                        if (subDealerResponse.status == "Success"){
                            if (subDealerResponse.data.size > 0){
                                AppController.retailerData = subDealerResponse.data
                                val adapter = RetailersListAdapter(context, AppController.retailerData)

                                recyclerList.adapter = adapter
                            }else{
                                recyclerList.adapter = null
                                CustomToast.customToast(context)
                                CustomToast.show(66)
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

                override fun onFailure(
                    call: Call<SubDealerRetailerMainResponseModel>,
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