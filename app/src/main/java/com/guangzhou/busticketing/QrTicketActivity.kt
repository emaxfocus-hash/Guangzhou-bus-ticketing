package com.guangzhou.busticketing

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QrTicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_ticket)

        val routeName = intent.getStringExtra("route_name") ?: "Selected route"
        val selectedSeats = intent.getStringExtra("selected_seats") ?: "1"
        val paymentMethod = intent.getStringExtra("payment_method") ?: "Mobile Money"
        val bookingCode = intent.getStringExtra("booking_code") ?: "GZB-000000"

        findViewById<android.widget.TextView>(R.id.qrRouteText).text = routeName
        findViewById<android.widget.TextView>(R.id.qrSeatsText).text = "Seats: $selectedSeats"
        findViewById<android.widget.TextView>(R.id.qrPaymentText).text = "Payment: $paymentMethod"
        findViewById<android.widget.TextView>(R.id.qrCodeText).text = "Booking code: $bookingCode"

        findViewById<Button>(R.id.downloadTicketButton).setOnClickListener {
            android.widget.Toast.makeText(this, "QR ticket ready for download", android.widget.Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.closeTicketButton).setOnClickListener {
            finish()
        }
    }
}
