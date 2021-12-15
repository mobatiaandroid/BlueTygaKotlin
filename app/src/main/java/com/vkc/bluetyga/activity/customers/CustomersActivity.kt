package com.vkc.bluetyga.activity.customers

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.customers.adapter.CustomersListAdapter
import com.vkc.bluetyga.activity.customers.model.get_customers.CustomersMainResponseModel
import com.vkc.bluetyga.activity.customers.model.get_customers.Response
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class CustomersActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var headerManager: HeaderManager
    lateinit var header: LinearLayout
    lateinit var imageBack: ImageView
    lateinit var customersRecyclerList: RecyclerView
    lateinit var progressBarDialog: ProgressBarDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_customers)
        context = this
        initialiseUI()
        getCustomers()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        customersRecyclerList = findViewById(R.id.recyclerViewCustomer)
        customersRecyclerList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        headerManager = HeaderManager(this@CustomersActivity,
            resources.getString(R.string.dealers))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        progressBarDialog = ProgressBarDialog(context)
        imageBack.setOnClickListener {
            finish()
        }
    }
    private fun getCustomers() {
        var customersMainResponse: CustomersMainResponseModel
        var customersResponse: Response
        AppController.customersList.clear()
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getCustomersResponse(
                PreferenceManager.getCustomerID(context)
            ).enqueue( object : Callback<CustomersMainResponseModel>{
                override fun onResponse(
                    call: Call<CustomersMainResponseModel>,
                    response: retrofit2.Response<CustomersMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        customersMainResponse = response.body()!!
                        customersResponse = customersMainResponse.response
                        if (customersResponse.status.equals("Success")){
                            if (customersResponse.data.isNotEmpty()){
                                for (i in customersResponse.data.indices) {
                                    val tempModel: com.vkc.bluetyga.activity.customers.model.get_customers.Data =
                                        com.vkc.bluetyga.activity.customers.model.get_customers.Data(
                                            "",
                                            "",
                                            "",
                                        )
                                    tempModel.phone = customersResponse.data[i].phone
                                    tempModel.name = customersResponse.data[i].name
                                    tempModel.role = customersResponse.data[i].role
                                    AppController.customersList.add(tempModel)
                                }
                                val adapter = CustomersListAdapter(context, AppController.customersList)
                                customersRecyclerList.adapter = adapter
                            }else{
                                CustomToast.customToast(context)
                                CustomToast.show(13)
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

                override fun onFailure(call: Call<CustomersMainResponseModel>, t: Throwable) {
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