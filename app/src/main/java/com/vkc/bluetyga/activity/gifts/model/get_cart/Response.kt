package com.vkc.bluetyga.activity.gifts.model.get_cart

data class Response(
    val balance_points: Int,
    val `data`: ArrayList<Data>,
    val status: String,
    val total_points: Int,
    val total_quantity: Int
)