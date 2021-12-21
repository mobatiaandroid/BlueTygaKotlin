package com.vkc.bluetyga.activity.point_history

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vkc.bluetyga.R
import com.vkc.bluetyga.activity.point_history.adapter.TransactionHistoryAdapter
import com.vkc.bluetyga.activity.point_history.model.transaction.Response
import com.vkc.bluetyga.activity.point_history.model.transaction.TransactionMainResponseModel
import com.vkc.bluetyga.api.ApiClient
import com.vkc.bluetyga.manager.AppController
import com.vkc.bluetyga.manager.HeaderManager
import com.vkc.bluetyga.manager.PreferenceManager
import com.vkc.bluetyga.utils.CustomToast
import com.vkc.bluetyga.utils.ProgressBarDialog
import com.vkc.bluetyga.utils.UtilityMethods
import retrofit2.Call
import retrofit2.Callback

class PointHistoryActivity : AppCompatActivity() {
    lateinit var context: Activity
    lateinit var header: LinearLayout
    lateinit var headerManager: HeaderManager
    lateinit var llSubDealer: LinearLayout
    lateinit var llRetailer: LinearLayout
    lateinit var llTransaction: LinearLayout
    lateinit var textEarnedCouponsSubDealer: TextView
    lateinit var textTransferredCouponsSubDealer: TextView
    lateinit var textBalance: TextView
    lateinit var textEarnedCouponsRetailer: TextView
    lateinit var textDealerCountRetailer: TextView
    lateinit var textCredit: TextView
    lateinit var textDebit: TextView
    lateinit var listViewHistory: ExpandableListView
    lateinit var imageBack: ImageView
    lateinit var progressBarDialog: ProgressBarDialog
    private var historyType = ""
    private var lastExpandedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_history)
        context = this
        initialiseUI()
    }

    private fun initialiseUI() {
        header = findViewById(R.id.header)
        llSubDealer = findViewById(R.id.llSubDealer)
        llRetailer = findViewById(R.id.llRetailer)
        textEarnedCouponsSubDealer = findViewById(R.id.textEarnedCouponsSubdealer)
        textTransferredCouponsSubDealer = findViewById(R.id.textTransferredCouponsSubDealer)
        textBalance = findViewById(R.id.textBalance)
        textEarnedCouponsRetailer = findViewById(R.id.textEarnedCouponsRetailer)
        textDealerCountRetailer = findViewById(R.id.textDealerCountRetailer)
        textCredit = findViewById(R.id.textCredit)
        textDebit = findViewById(R.id.textDebit)
        listViewHistory = findViewById(R.id.listViewHistory)
        progressBarDialog = ProgressBarDialog(context)
        headerManager =
            HeaderManager(this@PointHistoryActivity, resources.getString(R.string.point_history))
        headerManager.getHeader(header, 1)
        imageBack = headerManager.leftButton!!
        headerManager.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        llTransaction = findViewById(R.id.llTransactionType)
        imageBack.setOnClickListener { finish() }
        textCredit.setOnClickListener {
            historyType = "CREDIT"
            textCredit.setBackgroundResource(R.drawable.rounded_rect_green)
            textDebit.setBackgroundResource(R.drawable.rounded_rect_blue_line)
            val adapter = TransactionHistoryAdapter()
            listViewHistory.setAdapter(adapter)
            getHistory()
        }
        textDebit.setOnClickListener {
            historyType = "DEBIT"
            val adapter = TransactionHistoryAdapter()
            listViewHistory.setAdapter(adapter)
            textCredit.setBackgroundResource(R.drawable.rounded_rect_blue_line)
            textDebit.setBackgroundResource(R.drawable.rounded_rect_green)
            getHistory()
        }
        listViewHistory.setOnGroupExpandListener { groupPosition ->
            if (lastExpandedPosition != -1
                && groupPosition != lastExpandedPosition
            ) {
                listViewHistory.collapseGroup(lastExpandedPosition)
            }
            lastExpandedPosition = groupPosition
        }
        if (PreferenceManager.getUserType(context) == "7" || PreferenceManager.getUserType(context) == "6"
        ) {
            llTransaction.visibility = View.VISIBLE
            llRetailer.visibility = View.GONE
            llSubDealer.visibility = View.VISIBLE
            historyType = "CREDIT"
            getHistory()
        } else {
            llTransaction.visibility = View.GONE
            llRetailer.visibility = View.VISIBLE
            llSubDealer.visibility = View.GONE
            historyType = ""
            getHistory()
        }
    }

    private fun getHistory() {
        var transactionMainResponse: TransactionMainResponseModel
        var transactionResponse: Response
        if (UtilityMethods.checkInternet(context)){
            progressBarDialog.show()
            ApiClient.getApiService().getTransactionHistoryResponse(
                PreferenceManager.getCustomerID(context),
                PreferenceManager.getUserType(context),
                historyType
            ).enqueue( object : Callback<TransactionMainResponseModel>{
                override fun onResponse(
                    call: Call<TransactionMainResponseModel>,
                    response: retrofit2.Response<TransactionMainResponseModel>
                ) {
                    progressBarDialog.hide()
                    if (response.body() != null){
                        transactionMainResponse = response.body()!!
                        transactionResponse = transactionMainResponse.response
//                        val credits: String = transactionResponse.total_credits
//                        textEarnedCouponsRetailer.text = credits
                        textEarnedCouponsRetailer.text = transactionResponse.total_credits
                        textEarnedCouponsSubDealer.text = transactionResponse.total_credits
                        textTransferredCouponsSubDealer.text = transactionResponse.total_debits
                        textDealerCountRetailer.text = PreferenceManager.getDealerCount(context).toString()
                        textBalance.text = transactionResponse.balance_point
                        if (transactionResponse.status == "Success"){
                            if (transactionResponse.data.size > 0){
                                for (i in transactionResponse.data.indices) {
                                    if (!AppController.transactionData.contains(transactionResponse.data[i])){
                                        AppController.transactionData.add(transactionResponse.data[i])
                                    }
                                }
                                for (i in AppController.transactionData.indices){
                                    for (j in AppController.transactionData[i].details.indices){
                                        if (!AppController.transactionDetails.contains(AppController.transactionData[i].details[j])) {
                                            AppController.transactionDetails.add(AppController.transactionData[i].details[j])
                                        }
                                    }
                                }
                                textDealerCountRetailer.text = AppController.transactionData.size.toString()
                                val adapter = TransactionHistoryAdapter()
                                listViewHistory.setAdapter(adapter)
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

                override fun onFailure(call: Call<TransactionMainResponseModel>, t: Throwable) {
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