package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.guangzhou.busticketing.network.ApiClient
import com.guangzhou.busticketing.network.ApiService
import com.guangzhou.busticketing.network.models.PaymentStartRequest
import com.guangzhou.busticketing.network.models.PaymentStartResponse
import com.guangzhou.busticketing.network.models.PaymentStatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var api: ApiService
    private var polling = false
    private var paymentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        api = ApiClient.retrofit.create(ApiService::class.java)

        val routeName = intent.getStringExtra("route_name") ?: "Selected route"
        val selectedSeats = intent.getStringExtra("selected_seats") ?: "1"

        val routeText = findViewById<android.widget.TextView>(R.id.paymentRouteText)
        val seatsText = findViewById<android.widget.TextView>(R.id.paymentSeatsText)
        val radioGroup = findViewById<RadioGroup>(R.id.paymentMethodGroup)
        val confirmButton = findViewById<Button>(R.id.confirmPaymentButton)

        routeText.text = routeName
        seatsText.text = "Seats: $selectedSeats"

        confirmButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedRadio = findViewById<RadioButton>(selectedId)
            val paymentMethod = selectedRadio?.text?.toString() ?: "Mobile Money"

            if (paymentMethod == "Mobile Money") {
                startMobileMoney(routeName, selectedSeats)
            } else if (paymentMethod == "Cash on Board") {
                completeLocalBooking("Cash on Board")
            } else {
                // Card flow would go here
                Toast.makeText(this, "Card payments not implemented in this demo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMobileMoney(route: String, seats: String) {
        val req = PaymentStartRequest(route = route, seats = seats, amount = 32000, phone = getString(R.string.support_phone_1))
        api.startPayment(req).enqueue(object : Callback<PaymentStartResponse> {
            override fun onResponse(call: Call<PaymentStartResponse>, response: Response<PaymentStartResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    Toast.makeText(this@PaymentActivity, "Failed to start payment", Toast.LENGTH_SHORT).show()
                    return
                }

                paymentId = response.body()!!.payment_id
                Toast.makeText(this@PaymentActivity, "Payment started (id=${paymentId}), waiting for confirmation...", Toast.LENGTH_LONG).show()
                polling = true
                pollPaymentStatus()
            }

            override fun onFailure(call: Call<PaymentStartResponse>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun pollPaymentStatus() {
        val id = paymentId ?: return
        api.getPaymentStatus(id).enqueue(object : Callback<PaymentStatusResponse> {
            override fun onResponse(call: Call<PaymentStatusResponse>, response: Response<PaymentStatusResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    scheduleNextPoll()
                    return
                }

                val status = response.body()!!.status
                if (status == "paid") {
                    polling = false
                    completeLocalBooking("Mobile Money")
                } else if (status == "failed") {
                    polling = false
                    Toast.makeText(this@PaymentActivity, "Payment failed", Toast.LENGTH_SHORT).show()
                } else {
                    scheduleNextPoll()
                }
            }

            override fun onFailure(call: Call<PaymentStatusResponse>, t: Throwable) {
                scheduleNextPoll()
            }
        })
    }

    private fun scheduleNextPoll() {
        if (!polling) return
        handler.postDelayed({ pollPaymentStatus() }, 3000)
    }

    private fun completeLocalBooking(paymentMethod: String) {
        val intent = Intent(this, BookingConfirmationActivity::class.java)
        intent.putExtra("route_name", intent.getStringExtra("route_name") ?: "Selected route")
        intent.putExtra("selected_seats", intent.getStringExtra("selected_seats") ?: "1")
        intent.putExtra("payment_method", paymentMethod)
        intent.putExtra("booking_code", "GZB-${System.currentTimeMillis().toString().takeLast(6)}")
        startActivity(intent)
    }

    override fun onDestroy() {
        polling = false
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
