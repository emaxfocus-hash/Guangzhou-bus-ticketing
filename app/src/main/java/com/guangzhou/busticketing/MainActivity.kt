package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            val intent = Intent(this, RouteResultsActivity::class.java)
            intent.putExtra("from", "Dar es Salaam")
            intent.putExtra("to", "Morogoro")
            startActivity(intent)
        }
    }
}
