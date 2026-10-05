package com.guangzhou.busticketing.network.models

data class PaymentStartRequest(
    val route: String,
    val seats: String,
    val amount: Long,
    val phone: String
)
