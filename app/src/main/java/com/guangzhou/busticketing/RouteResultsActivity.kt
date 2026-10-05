package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RouteResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_results)

        val from = intent.getStringExtra("from") ?: "Dar es Salaam"
        val to = intent.getStringExtra("to") ?: "Morogoro"

        val routeTitle = findViewById<android.widget.TextView>(R.id.routeTitle)
        routeTitle.text = "Trips from $from to $to"

        val bookingButton1 = findViewById<Button>(R.id.routeBookButton1)
        val bookingButton2 = findViewById<Button>(R.id.routeBookButton2)
        val bookingButton3 = findViewById<Button>(R.id.routeBookButton3)

        val openSeatSelection: (String) -> Unit = { route ->
            val intent = Intent(this, SeatSelectionActivity::class.java)
            intent.putExtra("route_name", route)
            intent.putExtra("from", from)
            intent.putExtra("to", to)
            startActivity(intent)
        }

        bookingButton1.setOnClickListener { openSeatSelection("Dar es Salaam → Morogoro • Dala 94 Express") }
        bookingButton2.setOnClickListener { openSeatSelection("Dar es Salaam → Morogoro • Gani Bus") }
        bookingButton3.setOnClickListener { openSeatSelection("Dar es Salaam → Morogoro • Safari Coach") }
    }
}
