package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

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

            val intent = Intent(this, BookingConfirmationActivity::class.java)
            intent.putExtra("route_name", routeName)
            intent.putExtra("selected_seats", selectedSeats)
            intent.putExtra("payment_method", paymentMethod)
            intent.putExtra("booking_code", "GZB-${System.currentTimeMillis().toString().takeLast(6)}")
            startActivity(intent)
        }
    }
}
