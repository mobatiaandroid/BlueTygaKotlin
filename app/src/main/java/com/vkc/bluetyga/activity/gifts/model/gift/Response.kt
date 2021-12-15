package com.vkc.bluetyga.activity.gifts.model.gift

data class Response(
    val `data`: ArrayList<Data>,
    val status: String,
    val vouchers: ArrayList<Vouchers>
)