package com.example.wakadp1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
<<<<<<< HEAD
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

=======
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

>>>>>>> 30baf9b (Initial commit)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

<<<<<<< HEAD
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
=======
        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)

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
                        val user = auth.currentUser
                        user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val token: GetTokenResult? = tokenTask.result
                                val claims = token?.claims
                                if (claims == null || claims["role"] == "police") {
                                    // User has the correct role, proceed to Dashboard
                                    Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // User does not have the correct role
                                    Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                // Error getting token
                                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
>>>>>>> 30baf9b (Initial commit)
