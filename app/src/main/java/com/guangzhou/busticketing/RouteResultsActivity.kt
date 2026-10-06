package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.guangzhou.busticketing.data.TanzaniaRegions
import com.guangzhou.busticketing.data.TanzaniaRoutes

class RouteResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_results)

        val from = intent.getStringExtra("from") ?: "Dar es Salaam"
        val to = intent.getStringExtra("to") ?: "Morogoro"

        val routeTitle = findViewById<android.widget.TextView>(R.id.routeTitle)
        routeTitle.text = "Trips from $from to $to"

        val regionLabel = findViewById<android.widget.TextView>(R.id.regionLabel)
        regionLabel.text = "Regional coverage: ${TanzaniaRegions.regionFor(from)} → ${TanzaniaRegions.regionFor(to)}"

        val resultsContainer = findViewById<android.widget.LinearLayout>(R.id.routeResultsContainer)
        val routeMatches = TanzaniaRoutes.getRoutesFor(from, to).ifEmpty {
            listOf(
                TanzaniaRoutes.Route(from, to, "National Express", "09:00 AM", 41000, 5),
                TanzaniaRoutes.Route(from, to, "City Transit", "12:30 PM", 47000, 6)
            )
        }

        resultsContainer.removeAllViews()

        routeMatches.forEachIndexed { index, route ->
            val card = layoutInflater.inflate(R.layout.item_trip_card, resultsContainer, false)
            val title = card.findViewById<android.widget.TextView>(R.id.tripTitle)
            val details = card.findViewById<android.widget.TextView>(R.id.tripDetails)
            val bookButton = card.findViewById<Button>(R.id.tripBookButton)

            title.text = "${route.from} → ${route.to} • ${route.operator}"
            details.text = "Departure: ${route.departure} • Seats: ${route.seats} • Price: TSh ${route.price}"

            bookButton.setOnClickListener {
                val intent = Intent(this, SeatSelectionActivity::class.java)
                intent.putExtra("route_name", "${route.from} → ${route.to} • ${route.operator}")
                intent.putExtra("from", route.from)
                intent.putExtra("to", route.to)
                startActivity(intent)
            }

            resultsContainer.addView(card)
            if (index < routeMatches.lastIndex) {
                val spacing = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                spacing.setMargins(0, 0, 0, 16)
                card.layoutParams = spacing
            }
        }
    }
}
