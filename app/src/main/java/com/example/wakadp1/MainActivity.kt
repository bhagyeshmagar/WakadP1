package com.example.wakadp1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
        val officerId = prefs.getString("officer_id", null)
        
        if (officerId.isNullOrEmpty()) {
            // Not logged in, go to login
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // Already logged in, go to dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()
    }
}