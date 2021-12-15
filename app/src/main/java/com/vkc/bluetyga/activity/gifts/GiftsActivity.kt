package com.vkc.bluetyga.activity.gifts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.cart.CartActivity
import com.vkc.bluetyga.activity.gifts.adapter.GiftsAdapter
import com.vkc.bluetyga.activity.gifts.model.add_to_cart.AddToCartMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.cart_count.CartCountResponseModel
import com.vkc.bluetyga.activity.gifts.model.get_cart.GetCartMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.gift.Data
import com.vkc.bluetyga.activity.gifts.model.gift.GiftsMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.gift.Response
import com.vkc.bluetyga.activity.gifts.model.gift.Vouchers
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class GiftsActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var imageBack: ImageView
    lateinit var recyclerGifts: RecyclerView
    lateinit var editQuantity: EditText
    lateinit var imageCart: ImageView
    lateinit var textCart: TextView
    lateinit var llAddCart: LinearLayout
    lateinit var llVoucher: LinearLayout
    lateinit var spinnerVoucher: Spinner
    lateinit var textCoupon: TextView
    lateinit var textCartTotal: TextView
    lateinit var textBalanceCoupon: TextView
    lateinit var textCartQuantity: TextView
    lateinit var progressBarDialog: ProgressBarDialog
    var voucherId: String? = null
    var giftsData: ArrayList<Data> = ArrayList()
    var giftsVoucher: ArrayList<Vouchers> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifts)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        imageBack = findViewById(R.id.btn_left)
        recyclerGifts = findViewById(R.id.recyclerViewGifts)
        editQuantity = findViewById(R.id.editQty)
        imageCart = findViewById(R.id.btn_right)
        textCart = findViewById(R.id.textCount)
        llAddCart = findViewById(R.id.llCart)
        llVoucher = findViewById(R.id.llVoucher)
        spinnerVoucher = findViewById(R.id.spinnerVoucher)
        textCoupon = findViewById(R.id.textCoupon)
        textCartTotal = findViewById(R.id.textTotalCart)
        textCartQuantity = findViewById(R.id.textCartQuantity)
        textBalanceCoupon = findViewById(R.id.textBalanceCoupon)
        progressBarDialog = ProgressBarDialog(context)
        textCoupon.visibility = View.GONE
        llVoucher.visibility = View.GONE
        imageBack.setOnClickListener { finish() }
        imageCart.setOnClickListener {
            startActivity(
                Intent(
                    this@GiftsActivity,
                    CartActivity::class.java))
        }
        llAddCart.setOnClickListener {
            if (voucherId == "") {
                CustomToast.customToast(context)
                CustomToast.show(41)
            } else if (editQuantity.text.toString().trim { it <= ' ' } == "") {
                CustomToast.customToast(context)
                CustomToast.show(42)
            } else if (editQuantity.text.toString().trim { it <= ' ' } == "0") {
                CustomToast.customToast(context)
                CustomToast.show(59)
            } else {
                addToCart()
            }
        }
        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        //  LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        //  LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recyclerGifts.layoutManager = gridLayoutManager
        recyclerGifts.setHasFixedSize(true)
        spinnerVoucher.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                (parent!!.getChildAt(0) as TextView).gravity = Gravity.CENTER
                (parent.getChildAt(0) as TextView).textSize = 12f
                if (position > 0) {
                    voucherId = giftsVoucher[position - 1].id
                    textCoupon.text = "${giftsVoucher[position - 1].coupon_value} Coupons"
                    textCoupon.visibility = View.VISIBLE
                } else {
                    textCoupon.visibility = View.GONE
                    voucherId = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
            }
        }
        getGifts()
        getCartItems()
    }

    private fun addToCart() {
        var mainResponse: AddToCartMainResponseModel
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editQuantity.windowToken, 0)
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getAddToCartResponse(
                PreferenceManager.getCustomerID(context),
                voucherId!!,
                editQuantity.text.toString().trim { it <= ' ' },
                "2"
            ).enqueue(object : Callback<AddToCartMainResponseModel>{
                override fun onResponse(
                    call: Call<AddToCartMainResponseModel>,
                    response: retrofit2.Response<AddToCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        if (mainResponse.response == 1){
                            CustomToast.customToast(context)
                            CustomToast.show(6)
                            getCartItems()
                            editQuantity.setText("")
                            textCart.setText(mainResponse.cart_count)
                        }else if(mainResponse.response == 2){
                            CustomToast.customToast(context)
                            CustomToast.show(38)
                            editQuantity.setText("")
                        }else if(mainResponse.response == 5){
                            CustomToast.customToast(context)
                            CustomToast.show(61)
                            editQuantity.setText("")
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(39)
                            editQuantity.setText("")
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<AddToCartMainResponseModel>, t: Throwable) {
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

    private fun getGifts() {
        var giftsMainResponse: GiftsMainResponseModel
        var giftsResponse: Response

        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getGiftsResponse(
                PreferenceManager.getCustomerID(context)
            ).enqueue(object : Callback<GiftsMainResponseModel>{
                override fun onResponse(
                    call: Call<GiftsMainResponseModel>,
                    response: retrofit2.Response<GiftsMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        giftsMainResponse = response.body()!!
                        giftsResponse = giftsMainResponse.response
                        if (giftsResponse.status == "Success"){
                            getCartCount()
                            if (giftsResponse.data.size > 0){
                                giftsData = giftsResponse.data
                                val adapter = GiftsAdapter(
                                    context,
                                    giftsData,
                                    textCart,
                                    textBalanceCoupon,
                                    textCartTotal
                                )
                                recyclerGifts.adapter = adapter
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(4)
                            }
                            if (giftsResponse.vouchers.size > 0){
                                giftsVoucher = giftsResponse.vouchers
                                val listVoucher = ArrayList<String>()
                                listVoucher.add("Reward Points")
                                for (i in giftsVoucher.indices) {
                                    listVoucher.add(
                                        giftsVoucher[i].voucher_value
                                                + " ( "
                                                + giftsVoucher[i].coupon_value
                                                + " Coupons )")
                                }
                                if (listVoucher.size > 0) {
                                    llVoucher.visibility = View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this@GiftsActivity,
                                        android.R.layout.simple_spinner_item,
                                        listVoucher
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinnerVoucher.adapter = adapter
                                } else {
                                    llVoucher.visibility = View.GONE
                                }
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

                override fun onFailure(call: Call<GiftsMainResponseModel>, t: Throwable) {
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

    private fun getCartCount() {
        var mainResponse: CartCountResponseModel
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getCartCountResponse(
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<CartCountResponseModel>{
                override fun onResponse(
                    call: Call<CartCountResponseModel>,
                    response: retrofit2.Response<CartCountResponseModel>
                ) {
                    progressBarDialog.hide()
                    mainResponse = response.body()!!
                    if (mainResponse.response == 1){
                        textCart.text = mainResponse.cart_count.toString()
                    }else if (mainResponse.response == 5){
                        CustomToast.customToast(context)
                        CustomToast.show(62)
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(24)
                    }
                }

                override fun onFailure(call: Call<CartCountResponseModel>, t: Throwable) {
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
            ).enqueue( object : Callback<GetCartMainResponseModel>{
                override fun onResponse(
                    call: Call<GetCartMainResponseModel>,
                    response: retrofit2.Response<GetCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        cartResponse = mainResponse.response
                        if (cartResponse.status.equals("Success")){
                            val balancePoints: String = cartResponse.balance_points.toString()
                            val totalPoints: String = cartResponse.total_points.toString()
//                            val totalQuantity: String = cartResponse.total_quantity.toString()
                            textBalanceCoupon.text = balancePoints
                            textCartTotal.text = totalPoints
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
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