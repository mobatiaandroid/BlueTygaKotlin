package com.vkc.bluetyga.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private lateinit var apiService: ApiService

    // Dev URL
    private var BASE_URL = "http://dev.mobatia.com/vkc_apparel/apiv2/"

    // Live URL
//    var BASE_URL = "https://mobile.walkaroo.in/vkc_apparel/apiv2/";
//    var BASE_URL = "http://54.84.5.69/vkc/apiv2/";

    fun getApiService(): ApiService {
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
        return apiService
    }
}