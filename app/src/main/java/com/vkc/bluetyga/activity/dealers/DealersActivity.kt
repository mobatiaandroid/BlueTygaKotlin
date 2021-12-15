package com.vkc.bluetyga.activity.dealers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.dealers.adapter.DealersListAdapter
import com.vkc.bluetyga.activity.dealers.model.assign_dealers.AssignDealersMainResponseModel
import com.vkc.bluetyga.activity.dealers.model.assign_dealers.Response
import com.vkc.bluetyga.activity.dealers.model.get_dealers.Data
import com.vkc.bluetyga.activity.dealers.model.get_dealers.DealersMainResponseModel
import com.vkc.bluetyga.activity.home.HomeActivity
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import com.vkc.bluetyga.api.ApiClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class DealersActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var dealersRecyclerList: RecyclerView
    lateinit var headerManager: HeaderManager
    lateinit var header: LinearLayout
    lateinit var imageBack: ImageView
    lateinit var editSearch: EditText
    lateinit var textSubmit: TextView
    lateinit var progressBarDialog: ProgressBarDialog
    lateinit var adapter: DealersListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dealers)
        context = this
        initialiseUI()
        getDealers("")
    }

    private fun initialiseUI() {
        dealersRecyclerList = findViewById(R.id.recyclerViewDealer)
        header = findViewById(R.id.header)
        textSubmit = findViewById(R.id.textSubmit)
        editSearch = findViewById(R.id.editSearch)
        progressBarDialog = ProgressBarDialog(context)
        headerManager = HeaderManager(this@DealersActivity,
            resources.getString(R.string.dealers))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        imageBack.setOnClickListener {
            finish()
        }
        textSubmit.setOnClickListener {
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            for (i in 0 until AppController.dealersList.size) {
                if (AppController.dealersList[i].is_assigned == "1") {


                    val `object` = JSONObject()
                    `object`.put("id", AppController.dealersList[i].id)
                    `object`.put("role", AppController.dealersList[i].role)
                    jsonArray.put(`object`)
                }
            }
            jsonObject.put("dealers", jsonArray)
            if (jsonArray.length() > 0) {
                if (jsonArray.length() > 10) {
                    CustomToast.customToast(context)
                    CustomToast.show(11)
                } else {
                    submitDealers(jsonArray)
                }
            } else {
                CustomToast.customToast(context)
                CustomToast.show(10)
            }
        }
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try{
                    if (s!!.isEmpty()) {
                        dealersRecyclerList.scrollToPosition(0)
                    } else {
                        for (i in AppController.dealersList.indices) {
                            if (AppController.dealersList[i].name.lowercase().startsWith(s.toString().lowercase())) {
                                dealersRecyclerList.scrollToPosition(i)
                                break
                            }
                        }
                    }
                }catch (e:Exception){
                    Log.e("String",e.toString())
                }
            }
        })
    }

    private fun submitDealers(jsonArray: JSONArray) {
        var assignDealersMainResponse: AssignDealersMainResponseModel
        var assignDealersResponse: Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getSubmitDealersResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context),
                jsonArray.toString()
            ).enqueue( object : Callback<AssignDealersMainResponseModel>{
                override fun onResponse(
                    call: Call<AssignDealersMainResponseModel>,
                    response: retrofit2.Response<AssignDealersMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        assignDealersMainResponse = response.body()!!
                        assignDealersResponse = assignDealersMainResponse.response
                        if (assignDealersResponse.status == "Success"){
                            PreferenceManager.setLoginStatusFlag(context, "Y")
                            CustomToast.customToast(context)
                            CustomToast.show(12)
                            startActivity(Intent(this@DealersActivity,
                                HomeActivity::class.java))
                            finish()
                        }else{
                            PreferenceManager.setLoginStatusFlag(context, "N")
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<AssignDealersMainResponseModel>, t: Throwable) {
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

    private fun getDealers(searchKey: String) {
        var dealersMainResponse: DealersMainResponseModel
        var dealersResponse: com.vkc.bluetyga.activity.dealers.model.get_dealers.Response
        val dealersAssigned: ArrayList<Data> = ArrayList()
        val dealersNotAssigned: ArrayList<Data> = ArrayList()
        AppController.dealersList.clear()
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getDealersResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context),
                searchKey
            ).enqueue( object : Callback<DealersMainResponseModel>{
                override fun onResponse(
                    call: Call<DealersMainResponseModel>,
                    response: retrofit2.Response<DealersMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        dealersMainResponse = response.body()!!
                        dealersResponse = dealersMainResponse.response
                        if (dealersResponse.status == "Success"){
                            for (i in dealersResponse.data.indices){
                                val tempModel = Data("","","","")
                                tempModel.id = dealersResponse.data[i].id
                                tempModel.name = dealersResponse.data[i].name
                                tempModel.role = dealersResponse.data[i].role
                                tempModel.is_assigned = dealersResponse.data[i].is_assigned
                                if (dealersResponse.data[i].is_assigned == "0"){
                                    dealersNotAssigned.add(tempModel)
                                }else{
                                    dealersAssigned.add(tempModel)
                                }
                            }
                            AppController.dealersList.addAll(dealersAssigned)
                            AppController.dealersList.addAll(dealersNotAssigned)
                            adapter = DealersListAdapter(context, AppController.dealersList)
                            dealersRecyclerList.hasFixedSize()
                            dealersRecyclerList.layoutManager = LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL,
                                false
                            )

                            dealersRecyclerList.adapter = adapter
                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(0)
                        }
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<DealersMainResponseModel>, t: Throwable) {
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