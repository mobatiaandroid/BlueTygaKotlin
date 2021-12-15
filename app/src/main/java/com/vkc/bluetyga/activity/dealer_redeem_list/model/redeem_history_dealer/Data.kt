package com.vkc.bluetyga.activity.dealer_redeem_list.model.redeem_history_dealer

data class Data(
    val customer_id: String,
    val details: ArrayList<Detail>,
    val district: String,
    val name: String,
    val phone: String
)