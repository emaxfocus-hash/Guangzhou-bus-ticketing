package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.guangzhou.busticketing.data.TanzaniaRoutes

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fromInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.fromInput)
        val toInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.toInput)
        val searchButton = findViewById<Button>(R.id.searchButton)

        fromInput.setText("Dar es Salaam")
        toInput.setText("Morogoro")

        searchButton.setOnClickListener {
            val from = fromInput.text?.toString()?.trim().takeIf { it?.isNotEmpty() == true } ?: "Dar es Salaam"
            val to = toInput.text?.toString()?.trim().takeIf { it?.isNotEmpty() == true } ?: "Morogoro"

            val intent = Intent(this, RouteResultsActivity::class.java)
            intent.putExtra("from", from)
            intent.putExtra("to", to)
            startActivity(intent)
        }

        val popularRoutes = TanzaniaRoutes.getPopularRoutes()
        val popular1 = findViewById<Button>(R.id.popularRoute1)
        val popular2 = findViewById<Button>(R.id.popularRoute2)
        val popular3 = findViewById<Button>(R.id.popularRoute3)

        popular1.text = "${popularRoutes[0].from} → ${popularRoutes[0].to}"
        popular2.text = "${popularRoutes[1].from} → ${popularRoutes[1].to}"
        popular3.text = "${popularRoutes[2].from} → ${popularRoutes[2].to}"

        popular1.setOnClickListener {
            startRouteSearch(popularRoutes[0].from, popularRoutes[0].to)
        }
        popular2.setOnClickListener {
            startRouteSearch(popularRoutes[1].from, popularRoutes[1].to)
        }
        popular3.setOnClickListener {
            startRouteSearch(popularRoutes[2].from, popularRoutes[2].to)
        }
    }

    private fun startRouteSearch(from: String, to: String) {
        val intent = Intent(this, RouteResultsActivity::class.java)
        intent.putExtra("from", from)
        intent.putExtra("to", to)
        startActivity(intent)
    }
}
