package com.vkc.bluetyga.activity.cart.model.delete_cart

import com.vkc.bluetyga.activity.gifts.model.get_cart.Data

data class Response(
    val balance_points: Int,
    val status: String,
    val total_points: Int,
    val total_quantity: Int
)
