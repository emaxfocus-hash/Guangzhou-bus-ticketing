package com.guangzhou.busticketing

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CancelTicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_ticket)

        val bookingCode = intent.getStringExtra("booking_code") ?: "GZB-120540"
        val cancellationText = findViewById<android.widget.TextView>(R.id.cancelText)
        cancellationText.text = "Are you sure you want to cancel ticket $bookingCode?"

        findViewById<Button>(R.id.cancelConfirmButton).setOnClickListener {
            android.widget.Toast.makeText(this, "Ticket cancellation requested.", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.cancelBackButton).setOnClickListener { finish() }
    }
}
