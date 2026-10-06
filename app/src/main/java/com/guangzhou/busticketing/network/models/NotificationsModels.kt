package com.guangzhou.busticketing.network.models

data class DeviceRegisterRequest(
    val token: String,
    val user_phone: String?
)

data class DeviceRegisterResponse(
    val ok: Boolean
)

data class NotificationSendRequest(
    val title: String,
    val body: String,
    val tokens: List<String>
)

data class SmsSendRequest(
    val phone: String,
    val message: String
)

data class GenericResponse(
    val ok: Boolean,
    val message: String? = null
)
