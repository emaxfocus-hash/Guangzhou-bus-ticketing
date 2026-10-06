package com.guangzhou.busticketing.network

import com.guangzhou.busticketing.network.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("payments/start")
    fun startPayment(@Body request: PaymentStartRequest): Call<PaymentStartResponse>

    @GET("payments/{id}/status")
    fun getPaymentStatus(@Path("id") id: String): Call<PaymentStatusResponse>

    @POST("devices/register")
    fun registerDevice(@Body request: DeviceRegisterRequest): Call<DeviceRegisterResponse>

    @POST("notifications/send")
    fun sendNotification(@Body request: NotificationSendRequest): Call<GenericResponse>

    @POST("sms/send")
    fun sendSms(@Body request: SmsSendRequest): Call<GenericResponse>
}
