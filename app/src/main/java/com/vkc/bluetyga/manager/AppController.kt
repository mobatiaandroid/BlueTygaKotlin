package com.vkc.bluetyga.manager

import android.app.Application
import com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.Detail
import com.vkc.bluetyga.activity.dealers.model.get_dealers.Data
import com.vkc.bluetyga.activity.point_history.model.transaction.IndividualTransaction
import com.vkc.bluetyga.activity.point_history.model.transaction.TransactionHistory

class AppController: Application() {
    companion object{
        var instance: AppController? = null
        var dealersList: ArrayList<Data> = ArrayList()
        var customersList: ArrayList<com.vkc.bluetyga.activity.customers.model.get_customers.Data> =  ArrayList()
        var transactionData: ArrayList<TransactionHistory> = ArrayList()
        var transactionDetails: ArrayList<IndividualTransaction> = ArrayList()
        var notificationsList: ArrayList<com.vkc.bluetyga.activity.inbox.model.inbox.Data> = ArrayList()
        var redeemHistoryList: ArrayList<com.vkc.bluetyga.activity.sub_dealer_redeem.model.redeem_history.Data> = ArrayList()
        var redeemHistoryDataDealer: ArrayList<com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer.Data> = ArrayList()
        var redeemHistoryDetailDealer: ArrayList<Detail> = ArrayList()
        var redeemReportData: ArrayList<com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Data> = ArrayList()
        var redeemReportDetail: ArrayList<com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report.Detail> = ArrayList()


        fun applicationContext() : AppController {
            return instance as AppController
        }
    }
    init {
        instance = this
    }

}