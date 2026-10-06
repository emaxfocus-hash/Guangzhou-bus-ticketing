package com.guangzhou.busticketing

import android.util.Log
import com.guangzhou.busticketing.network.ApiClient
import com.guangzhou.busticketing.network.ApiService
import com.guangzhou.busticketing.network.models.DeviceRegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DeviceRegistrar {
    private val api = ApiClient.retrofit.create(ApiService::class.java)

    fun register(token: String, phone: String? = null) {
        val req = DeviceRegisterRequest(token = token, user_phone = phone)
        api.registerDevice(req).enqueue(object : Callback<com.guangzhou.busticketing.network.models.DeviceRegisterResponse> {
            override fun onResponse(call: Call<com.guangzhou.busticketing.network.models.DeviceRegisterResponse>, response: Response<com.guangzhou.busticketing.network.models.DeviceRegisterResponse>) {
                Log.d("DeviceRegistrar", "registered device: ${response.body()?.ok}")
            }

            override fun onFailure(call: Call<com.guangzhou.busticketing.network.models.DeviceRegisterResponse>, t: Throwable) {
                Log.w("DeviceRegistrar", "failed to register device: ${t.message}")
            }
        })
    }
}
