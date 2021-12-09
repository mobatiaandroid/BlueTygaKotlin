package com.vkc.bluetyga.manager

import android.app.Application
import com.vkc.bluetyga.activity.dealers.model.get_dealers.Data

class AppController: Application() {
    companion object{
        var instance: AppController? = null
        var dealersList: ArrayList<Data> = ArrayList()

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