package com.vkc.bluetyga.activity.inbox

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.inbox.adapter.InboxAdapter
import com.vkc.bluetyga.activity.inbox.model.inbox.Data
import com.vkc.bluetyga.activity.inbox.model.inbox.NotificationMainResponseModel
import com.vkc.bluetyga.activity.inbox.model.inbox.Response
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class InboxActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var headerManager: HeaderManager
    lateinit var imageBack: ImageView
    lateinit var recyclerInboxList: RecyclerView
    lateinit var progressBarDialog: ProgressBarDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        recyclerInboxList = findViewById(R.id.recyclerListInbox)
        progressBarDialog = ProgressBarDialog(context)
        headerManager = HeaderManager(this@InboxActivity, resources.getString(R.string.inbox))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        progressBarDialog = ProgressBarDialog(context)
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        imageBack.setOnClickListener {
            finish()
        }
        getInbox()
    }

    private fun getInbox() {
        AppController.notificationsList.clear()
        var notificationMainResponse: NotificationMainResponseModel
        var notificationResponse: Response
        var notificationData: ArrayList<Data>
        var tempModel = Data("","","", "",
            "", "","","")
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getNotificationResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context)
            ).enqueue(object : Callback<NotificationMainResponseModel> {
                override fun onResponse(
                    call: Call<NotificationMainResponseModel>,
                    response: retrofit2.Response<NotificationMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    notificationMainResponse = response.body()!!
                    notificationResponse = notificationMainResponse.response
                    if (notificationResponse.status.equals("Success")){
                        notificationData = notificationResponse.data
                        if (notificationData.size > 0){
                            for (i in notificationData.indices) {
                                tempModel = Data("","","","",
                                    "","","","")
                                tempModel.title = notificationData[i].title
                                tempModel.message = notificationData[i].message
                                tempModel.image = notificationData[i].image
                                tempModel.createdon = notificationData[i].createdon
                                tempModel.from_date = notificationData[i].from_date
                                tempModel.to_date = notificationData[i].to_date
                                tempModel.pdf = notificationData[i].pdf
                                tempModel.notification_type = notificationData[i].notification_type
                                AppController.notificationsList.add(tempModel)
                            }

                        }else{
                            CustomToast.customToast(context)
                            CustomToast.show(50)
                        }
                        Log.e("Notification", AppController.notificationsList.toString())
                        val adapter = InboxAdapter(context, AppController.notificationsList)
                        recyclerInboxList.hasFixedSize()
                        recyclerInboxList.layoutManager = LinearLayoutManager(
                            context,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        recyclerInboxList.adapter = adapter
                    }else{
                        CustomToast.customToast(context)
                        CustomToast.show(0)
                    }
                }

                override fun onFailure(call: Call<NotificationMainResponseModel>, t: Throwable) {
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