package com.vkc.bluetyga.utils

import android.content.Context
import android.net.ConnectivityManager

class UtilityMethods {
    companion object{
        fun checkInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }


    }
}