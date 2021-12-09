package com.vkc.bluetyga.manager

import android.app.Application
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


        fun applicationContext() : AppController {
            return instance as AppController
        }
    }
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}