package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SeatSelectionActivity : AppCompatActivity() {
    private val selectedSeats = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)

        val routeName = intent.getStringExtra("route_name") ?: "Selected route"
        val routeLabel = findViewById<android.widget.TextView>(R.id.selectedRouteText)
        routeLabel.text = routeName

        val seatButtons = listOf(
            findViewById<Button>(R.id.seat1),
            findViewById<Button>(R.id.seat2),
            findViewById<Button>(R.id.seat3),
            findViewById<Button>(R.id.seat4),
            findViewById<Button>(R.id.seat5),
            findViewById<Button>(R.id.seat6),
            findViewById<Button>(R.id.seat7),
            findViewById<Button>(R.id.seat8),
            findViewById<Button>(R.id.seat9),
            findViewById<Button>(R.id.seat10),
            findViewById<Button>(R.id.seat11),
            findViewById<Button>(R.id.seat12)
        )

        val selectedSeatsText = findViewById<android.widget.TextView>(R.id.selectedSeatsText)
        val continueButton = findViewById<Button>(R.id.continuePaymentButton)

        seatButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val seatNumber = index + 1
                if (selectedSeats.contains(seatNumber)) {
                    selectedSeats.remove(seatNumber)
                    button.setBackgroundColor(getColor(R.color.primary_blue))
                } else {
                    selectedSeats.add(seatNumber)
                    button.setBackgroundColor(getColor(R.color.accent_orange))
                }

                selectedSeatsText.text = if (selectedSeats.isEmpty()) {
                    "No seats selected yet"
                } else {
                    "Selected seats: ${selectedSeats.sorted().joinToString(", ") }"
                }
            }
        }

        continueButton.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                android.widget.Toast.makeText(this, "Please select at least one seat.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("route_name", routeName)
            intent.putExtra("selected_seats", selectedSeats.sorted().joinToString(","))
            startActivity(intent)
        }
    }
}
