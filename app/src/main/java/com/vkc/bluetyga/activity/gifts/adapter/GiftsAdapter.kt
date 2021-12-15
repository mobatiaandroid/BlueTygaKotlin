package com.vkc.bluetyga.activity.gifts.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.gifts.model.add_to_cart.AddToCartMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.get_cart.GetCartMainResponseModel
import com.vkc.bluetyga.activity.gifts.model.gift.Data
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GiftsAdapter(
    context: Activity,
    giftsData: ArrayList<Data>,
    textCart: TextView,
    textBalanceCoupon: TextView,
    textCartTotal: TextView
): RecyclerView.Adapter<GiftsAdapter.ViewHolder>() {
    var mContext = context
    var giftsDataList = giftsData
    var mTextCart = textCart
    var mTextBalanceCoupon = textBalanceCoupon
    var mTextCartTotal = textCartTotal
    var progressBarDialog: ProgressBarDialog = ProgressBarDialog(mContext)
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null
        var points: TextView? = null
        var image: ImageView? = null
        var llCart: LinearLayout? = null
        var editQty: EditText? = null
        init {
            name = itemView.findViewById(R.id.name)
            points = itemView.findViewById(R.id.point)
            image = itemView.findViewById(R.id.image)
            llCart = itemView.findViewById(R.id.llCart)
            editQty = itemView.findViewById(R.id.editQty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name!!.text = giftsDataList[position].title
        holder.points!!.text = "Coupons :${giftsDataList[position].point}"
        val giftName: String = giftsDataList[position].image.replace(" ", "%20")
        if (giftName.isNotEmpty()) {
            Glide.with(mContext).load(giftName).centerInside()
                .into(holder.image!!)
        } else {
            Glide.with(mContext).load(R.drawable.gift_default).centerInside()
                .into(holder.image!!)
        }
        holder.llCart!!.setOnClickListener {
            if (holder.editQty!!.text.toString().trim { it <= ' ' } == "") {
                CustomToast.customToast(mContext)
                CustomToast.show(40)
            } else if (holder.editQty!!.text.toString().trim { it <= ' ' } == "0") {
                CustomToast.customToast(mContext)
                CustomToast.show(59)
            } else {
                addToCart(holder.editQty!!, giftsDataList[position].id)
            }
        }
    }

    private fun addToCart(qty: EditText, id: String) {
        var mainResponse: AddToCartMainResponseModel
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(qty.windowToken, 0)
        if (UtilityMethods.checkInternet(mContext)){
            progressBarDialog.show()
            ApiClient.getApiService().getAddToCartResponse(
                PreferenceManager.getCustomerID(mContext),
                id,
                qty.text.toString().trim { it <= ' ' },
                "1"
            ).enqueue(object : Callback<AddToCartMainResponseModel>{
                override fun onResponse(
                    call: Call<AddToCartMainResponseModel>,
                    response: Response<AddToCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        if (mainResponse.response == 1){
                            CustomToast.customToast(mContext)
                            CustomToast.show(6)
                            getCartItems()
                            qty.setText("")
                            mTextCart.setText(mainResponse.cart_count)
                        }else if(mainResponse.response == 2){
                            CustomToast.customToast(mContext)
                            CustomToast.show(38)
                            qty.setText("")
                        }else if(mainResponse.response == 5){
                            CustomToast.customToast(mContext)
                            CustomToast.show(61)
                            qty.setText("")
                        }else{
                            CustomToast.customToast(mContext)
                            CustomToast.show(39)
                            qty.setText("")
                        }
                    }else{
                        CustomToast.customToast(mContext)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<AddToCartMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(mContext)
                    CustomToast.show(0)
                }
            })
        }else{
            CustomToast.customToast(mContext)
            CustomToast.show(58)
        }
    }

    private fun getCartItems() {
        var mainResponse: GetCartMainResponseModel
        var cartResponse: com.vkc.bluetyga.activity.gifts.model.get_cart.Response
        if (UtilityMethods.checkInternet(mContext)){
            progressBarDialog.show()
            ApiClient.getApiService().getCartResponse(
                PreferenceManager.getCustomerID(mContext)
            ).enqueue( object : Callback<GetCartMainResponseModel>{
                override fun onResponse(
                    call: Call<GetCartMainResponseModel>,
                    response: Response<GetCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        cartResponse = mainResponse.response
                        if (cartResponse.status == "Success"){
                            val balancePoints: String = cartResponse.balance_points.toString()
                            val totalPoints: String = cartResponse.total_points.toString()
//                            val totalQuantity: String = cartResponse.total_quantity.toString()
                            mTextBalanceCoupon.text = balancePoints
                            mTextCartTotal.text = totalPoints
                        }else{
                            CustomToast.customToast(mContext)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(mContext)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<GetCartMainResponseModel>, t: Throwable) {
                    progressBarDialog.hide()
                    CustomToast.customToast(mContext)
                    CustomToast.show(0)
                }
            })
        }else{
            CustomToast.customToast(mContext)
            CustomToast.show(58)
        }
    }

    override fun getItemCount(): Int {
        return giftsDataList.size
    }
}