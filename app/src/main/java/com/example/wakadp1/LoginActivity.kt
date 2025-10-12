package com.example.wakadp1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etPoliceId = findViewById<TextInputEditText>(R.id.etPoliceId)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val id = etPoliceId.text?.toString()?.trim().orEmpty()
            val pw = etPassword.text?.toString().orEmpty()
            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "Enter credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // For demo: accept any credentials (you can replace with real auth)
            val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
            prefs.edit().putString("officer_id", id).putString("officer_name", "Officer $id").apply()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}
