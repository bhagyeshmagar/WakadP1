package com.example.wakadp1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wakadp1.utils.BiometricHelper
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("wakadp1_prefs", MODE_PRIVATE)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val btnBiometric = findViewById<MaterialButton>(R.id.btnBiometric)

        // Show biometric button only if:
        // 1. Device supports biometric
        // 2. User has logged in before (we have saved credentials indicator)
        val hasLoggedInBefore = prefs.getBoolean("has_logged_in", false)
        if (BiometricHelper.canAuthenticate(this) && hasLoggedInBefore && auth.currentUser != null) {
            btnBiometric.visibility = View.VISIBLE
            btnBiometric.setOnClickListener {
                showBiometricPrompt()
            }
            // Auto-show biometric prompt for returning users
            showBiometricPrompt()
        } else {
            btnBiometric.visibility = View.GONE
        }

        login.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Save login state for biometric next time
                        prefs.edit().putBoolean("has_logged_in", true).apply()
                        
                        Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        findViewById<TextView>(R.id.tvSignup).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun showBiometricPrompt() {
        BiometricHelper.showPrompt(
            activity = this,
            title = "Maharashtra Police",
            subtitle = "Use fingerprint to unlock",
            onSuccess = {
                Toast.makeText(this, "Biometric verified!", Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            },
            onError = { error ->
                Toast.makeText(this, "Biometric failed: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}
