package com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_report

data class Data(
    val cust_id: String,
    val details: ArrayList<Detail>,
    val name: String,
    val phone: String,
    val place: String
)