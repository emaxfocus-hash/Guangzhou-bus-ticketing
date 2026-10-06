package com.guangzhou.busticketing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }

        val editProfileButton = findViewById<Button>(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            android.widget.Toast.makeText(this, "Profile editing is available in the next version.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
