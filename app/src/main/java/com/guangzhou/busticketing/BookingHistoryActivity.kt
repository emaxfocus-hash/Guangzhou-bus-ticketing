package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BookingHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)

        val historyContainer = findViewById<android.widget.LinearLayout>(R.id.bookingHistoryContainer)
        val sampleBookings = listOf(
            BookingItem("GZB-120540", "Dar es Salaam → Morogoro", "Seats: 1, 3", "Paid via Mobile Money", "2026-10-06"),
            BookingItem("GZB-220910", "Dodoma → Arusha", "Seats: 5", "Paid via Cash on Board", "2026-10-04"),
            BookingItem("GZB-330910", "Arusha → Moshi", "Seats: 2", "Paid via Mobile Money", "2026-10-02")
        )

        sampleBookings.forEach { booking ->
            val card = layoutInflater.inflate(R.layout.item_booking_history, historyContainer, false)
            val code = card.findViewById<android.widget.TextView>(R.id.bookingCode)
            val route = card.findViewById<android.widget.TextView>(R.id.bookingRoute)
            val details = card.findViewById<android.widget.TextView>(R.id.bookingDetails)
            val payment = card.findViewById<android.widget.TextView>(R.id.bookingPayment)
            val date = card.findViewById<android.widget.TextView>(R.id.bookingDate)

            code.text = booking.code
            route.text = booking.route
            details.text = booking.seats
            payment.text = booking.payment
            date.text = booking.date

            historyContainer.addView(card)
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    data class BookingItem(
        val code: String,
        val route: String,
        val seats: String,
        val payment: String,
        val date: String
    )
}
