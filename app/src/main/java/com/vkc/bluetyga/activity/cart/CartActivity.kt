package com.vkc.bluetyga.activity.cart

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
import com.vkc.bluetyga.activity.cart.adapter.CartListAdapter
import com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.DealerSubDealerMainResponseModel
import com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.Response
import com.vkc.bluetyga.activity.cart.model.place_order.PlaceOrderMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.get_cart.Data
import com.vkc.bluetyga.activity.gifts.model.get_cart.GetCartMainResponseModel
import com.vkc.bluetyga.activity.redeem.RedeemHistoryActivity
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class CartActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var recyclerCart: RecyclerView
    lateinit var imageBack: ImageView
    private lateinit var imageHistory: ImageView
    private lateinit var buttonOrder: Button
    lateinit var textCartTotal: TextView
    lateinit var textBalanceCoupon: TextView
    lateinit var textCartQuantity: TextView
    lateinit var spinnerDealer: Spinner
    lateinit var progressBarDialog: ProgressBarDialog
    var cartData: ArrayList<Data> = ArrayList()
    var dealerData: ArrayList<com.vkc.bluetyga.activity.cart.model.dealer_sub_dealer.Data> = ArrayList()
    var dealerId: String? = null
    var role: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        recyclerCart = findViewById(R.id.recyclerCart)
        imageBack = findViewById(R.id.btn_left)
        imageHistory = findViewById(R.id.btn_right)
        buttonOrder = findViewById(R.id.buttonOrder)
        textCartTotal = findViewById(R.id.textTotalCart)
        textBalanceCoupon = findViewById(R.id.textBalanceCoupon)
        textCartQuantity = findViewById(R.id.textCartQuantity)
        spinnerDealer = findViewById(R.id.spinnerDealer)
        progressBarDialog = ProgressBarDialog(context)
        imageBack.setOnClickListener {
            finish()
        }
        buttonOrder.setOnClickListener {
            if (dealerId == "") {
                CustomToast.customToast(context)
                CustomToast.show(45)
            } else if (cartData.size == 0) {
                CustomToast.customToast(context)
                CustomToast.show(46)
            } else {
                val listOrder = ArrayList<String>()
                for (i in cartData.indices) {
                    listOrder.add(cartData[i].id)
                }
                val gson = GsonBuilder().create()
                val details = gson.toJsonTree(listOrder).asJsonArray
                placeOrder(details.toString())
            }
        }
        imageHistory.setOnClickListener {
            startActivity(
                Intent(
                    this@CartActivity,
                    RedeemHistoryActivity::class.java
                )
            )

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
                    dealerId = dealerData[position - 1].id
                    role = dealerData[position - 1].role
                } else {
                    dealerId = ""
                    role = ""
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        getDealers()
        getCartItems()
    }

    private fun placeOrder(json: String) {
        var mainResponse: PlaceOrderMainResponseModel
        var orderResponse: com.vkc.bluetyga.activity.cart.model.place_order.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().placeOrder(
                PreferenceManager.getCustomerID(context),
                dealerId.toString(),
                role.toString(), json
            ).enqueue( object : Callback<PlaceOrderMainResponseModel>{
                override fun onResponse(
                    call: Call<PlaceOrderMainResponseModel>,
                    response: retrofit2.Response<PlaceOrderMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        orderResponse = mainResponse.response
                        if (orderResponse.status == "Success"){
                            CustomToast.customToast(context)
                            CustomToast.show(47)
                            spinnerDealer.setSelection(0)
                            dealerId = ""
                            getCartItems()
                        }else if (orderResponse.status.equals("scheme_error", ignoreCase = true)){
                            CustomToast.customToast(context)
                            CustomToast.show(64)
                            spinnerDealer.setSelection(0)
                            dealerId = ""
                            getCartItems()
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(48)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<PlaceOrderMainResponseModel>, t: Throwable) {
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
                PreferenceManager.getUserType(context)
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
                            if (dealerResponse.data.isNotEmpty()){
                                dealerData = dealerResponse.data
                                val listDealer = ArrayList<String>()
                                listDealer.add("Select Dealer")
                                for (i in dealerData.indices) {
                                    listDealer.add(dealerData[i].name)
                                }
                                val adapter = ArrayAdapter(
                                    this@CartActivity,
                                    android.R.layout.simple_spinner_item,
                                    listDealer
                                )
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerDealer.adapter = adapter
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(44)
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

    private fun getCartItems() {
        var mainResponse: GetCartMainResponseModel
        var cartResponse: com.vkc.bluetyga.activity.gifts.model.get_cart.Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getCartResponse(
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<GetCartMainResponseModel> {
                override fun onResponse(
                    call: Call<GetCartMainResponseModel>,
                    response: retrofit2.Response<GetCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        cartResponse = mainResponse.response
                        if (cartResponse.status == "Success"){
                            val balancePoints: String = cartResponse.balance_points.toString()
                            val totalPoints: String = cartResponse.total_points.toString()
                            val totalQuantity: String = cartResponse.total_quantity.toString()
                            textBalanceCoupon.text = balancePoints
                            textCartTotal.text = totalPoints
                            textCartQuantity.text = totalQuantity
                            if (cartResponse.data.size > 0){
                                cartData = cartResponse.data
                                val adapter = CartListAdapter(
                                    context,
                                    cartData,
                                    textCartTotal,
                                    textBalanceCoupon,
                                    textCartQuantity,
                                    recyclerCart
                                )
                                recyclerCart.layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                recyclerCart.adapter = adapter
                            }else{
                                recyclerCart.adapter = null
                                CustomToast.customToast(context)
                                CustomToast.show(43)
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

                override fun onFailure(call: Call<GetCartMainResponseModel>, t: Throwable) {
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