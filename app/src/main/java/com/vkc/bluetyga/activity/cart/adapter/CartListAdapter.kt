package com.vkc.bluetyga.activity.cart.adapter

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.cart.model.delete_cart.DeleteCartMainResponseModel
import com.vkc.bluetyga.activity.cart.model.edit_cart.EditCartMainResponseModel
import com.vkc.bluetyga.activity.cart.model.edit_cart.Response
import com.vkc.bluetyga.activity.gifts.model.get_cart.Data
import com.vkc.bluetyga.activity.gifts.model.get_cart.GetCartMainResponseModel
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class CartListAdapter(
    context: Activity,
    cartData: ArrayList<Data>,
    textCartTotal: TextView,
    textBalanceCoupon: TextView,
    textCartQuantity: TextView,
    recyclerCart: RecyclerView
): RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    var mContext = context
    var mCartData = cartData
    var mTextCartTotal = textCartTotal
    var mTextBalanceCoupon = textBalanceCoupon
    var mTextCartQuantity = textCartQuantity
    var mRecyclerCart = recyclerCart
    var progressBarDialog = ProgressBarDialog(context)
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textType: TextView? = null
        var textTitle: TextView? = null
        var textCoupon: TextView? = null
        var editQuantity: EditText? = null
        var imageRemove: ImageView? = null
        init {
            textType = itemView.findViewById<View>(R.id.textType) as TextView?
            textTitle = itemView.findViewById<View>(R.id.textName) as TextView?
            textCoupon = itemView.findViewById<View>(R.id.textCoupons) as TextView?
            editQuantity = itemView.findViewById<View>(R.id.editQuantity) as EditText?
            imageRemove = itemView.findViewById<View>(R.id.imageDelete) as ImageView?
            editQuantity!!.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textType!!.text = mCartData[holder.adapterPosition].gift_type
        holder.textTitle!!.text = mCartData[holder.adapterPosition].gift_title
        holder.textCoupon!!.text = mCartData[holder.adapterPosition].point
        holder.editQuantity!!.setText(mCartData[holder.adapterPosition].quantity)


        holder.editQuantity!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count >= 1) {
                    if (mCartData[holder.adapterPosition].quantity == s.toString()) {
                        /***Do Nothing***/
                    } else {
                        if (s.toString() == "0") {
                            CustomToast.customToast(mContext)
                            CustomToast.show(59)
                            getCartItems()
                        } else {
                            editCart(mCartData[holder.adapterPosition].id, s.toString())
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        holder.editQuantity!!.setOnEditorActionListener { v, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                editCart(
                    mCartData[holder.adapterPosition].id,
                    holder.editQuantity!!.text.toString()
                )
                handled = true
            }
            handled
        }
        holder.imageRemove!!.setOnClickListener {
            val listDel = java.util.ArrayList<String>()
            listDel.add(mCartData[holder.adapterPosition].id)
            val gson = GsonBuilder().create()
            val details = gson.toJsonTree(listDel).asJsonArray
            // System.out.println("JsonArray:" + details);
            deleteCart(details.toString())
        }
    }

    private fun deleteCart(listDelete: String) {
        var mainResponse: DeleteCartMainResponseModel
        var deleteResponse: com.vkc.bluetyga.activity.cart.model.delete_cart.Response
        if (UtilityMethods.checkInternet(mContext)){
            progressBarDialog.show()
            ApiClient.getApiService().getDeleteCartResponse(
                PreferenceManager.getCustomerID(mContext),
                listDelete
            ).enqueue( object : Callback<DeleteCartMainResponseModel>{
                override fun onResponse(
                    call: Call<DeleteCartMainResponseModel>,
                    response: retrofit2.Response<DeleteCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        deleteResponse = mainResponse.response
                        if (deleteResponse.status == "Success"){
                            mRecyclerCart.adapter = null
                            mTextCartTotal.text = (deleteResponse.total_points.toString())
                            mTextBalanceCoupon.text = (deleteResponse.balance_points.toString())
                            mTextCartQuantity.text = (deleteResponse.total_quantity.toString())
                            getCartItems()
                        }else{
                            CustomToast.customToast(mContext)
                            CustomToast.show(49)
                        }
                    }else{
                        CustomToast.customToast(mContext)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<DeleteCartMainResponseModel>, t: Throwable) {
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
        var cartData: ArrayList<Data>
        if (UtilityMethods.checkInternet(mContext)){
            progressBarDialog.show()
            ApiClient.getApiService().getCartResponse(
                PreferenceManager.getCustomerID(mContext)
            ).enqueue( object : Callback<GetCartMainResponseModel>{
                override fun onResponse(
                    call: Call<GetCartMainResponseModel>,
                    response: retrofit2.Response<GetCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        cartResponse = mainResponse.response
                        if (cartResponse.data.size > 0){
                            cartData = cartResponse.data
                            mRecyclerCart.adapter = null
                            val adapter = CartListAdapter(
                                mContext,
                                cartData,
                                mTextCartTotal,
                                mTextBalanceCoupon,
                                mTextCartQuantity,
                                mRecyclerCart
                            )

                            mRecyclerCart.adapter = adapter
                        }else{
                            mRecyclerCart.adapter = null
                            CustomToast.customToast(mContext)
                            CustomToast.show(43)

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


    private fun editCart(id: String, quantity: String) {
        var mainResponse: EditCartMainResponseModel
        var editCartResponse: Response
        if (UtilityMethods.checkInternet(mContext)){
            progressBarDialog.show()
            ApiClient.getApiService().getEditCartResponse(
                PreferenceManager.getCustomerID(mContext), id, quantity
            ).enqueue( object : Callback<EditCartMainResponseModel>{
                override fun onResponse(
                    call: Call<EditCartMainResponseModel>,
                    response: retrofit2.Response<EditCartMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        mainResponse = response.body()!!
                        editCartResponse = mainResponse.response
                        if (editCartResponse.status == "Success") {
                            mTextCartTotal.text = (editCartResponse.total_points.toString())
                            mTextBalanceCoupon.text = (editCartResponse.balance_points.toString())
                            mTextCartQuantity.text = editCartResponse.total_quantity

                            getCartItems()
                        }else if (editCartResponse.status == "scheme_error"){
                            CustomToast.customToast(mContext)
                            CustomToast.show(63)
                        }else{
                            CustomToast.customToast(mContext)
                            CustomToast.show(38)
                        }
                    }else{
                        CustomToast.customToast(mContext)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<EditCartMainResponseModel>, t: Throwable) {
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
        TODO("Not yet implemented")
    }
}