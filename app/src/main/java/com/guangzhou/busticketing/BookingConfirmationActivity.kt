package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BookingConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        val routeName = intent.getStringExtra("route_name") ?: "Selected route"
        val selectedSeats = intent.getStringExtra("selected_seats") ?: "1"
        val paymentMethod = intent.getStringExtra("payment_method") ?: "Mobile Money"
        val bookingCode = intent.getStringExtra("booking_code") ?: "GZB-000000"

        findViewById<android.widget.TextView>(R.id.confirmationRouteText).text = routeName
        findViewById<android.widget.TextView>(R.id.confirmationSeatsText).text = "Seats: $selectedSeats"
        findViewById<android.widget.TextView>(R.id.confirmationPaymentText).text = "Payment: $paymentMethod"
        findViewById<android.widget.TextView>(R.id.confirmationCodeText).text = "Booking code: $bookingCode"

        findViewById<Button>(R.id.backToHomeButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
