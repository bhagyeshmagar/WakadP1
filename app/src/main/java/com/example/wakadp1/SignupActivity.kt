package com.example.wakadp1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.PoliceData
import com.example.wakadp1.data.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    // Inputs
    private lateinit var etName: TextInputEditText
    private lateinit var etDob: TextInputEditText
    private lateinit var etMobile: TextInputEditText
    private lateinit var etBuckleNo: TextInputEditText
    private lateinit var etJoiningDate: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var spinnerBloodGroup: Spinner
    private lateinit var spinnerRank: Spinner
    private lateinit var spinnerDistrict: Spinner
    private lateinit var spinnerStation: Spinner
    private lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize Views directly
        etName = findViewById(R.id.etName)
        etDob = findViewById(R.id.etDob)
        etMobile = findViewById(R.id.etMobile)
        etBuckleNo = findViewById(R.id.etBuckleNo)
        etJoiningDate = findViewById(R.id.etJoiningDate)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup)
        spinnerRank = findViewById(R.id.spinnerRank)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        spinnerStation = findViewById(R.id.spinnerStation)
        btnRegister = findViewById(R.id.btnRegister)

        setupSpinners()
        setupDatePickers()

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setupSpinners() {
        // Blood Group
        val bloodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PoliceData.bloodGroups)
        spinnerBloodGroup.adapter = bloodAdapter

        // Rank
        val rankAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PoliceData.ranks)
        spinnerRank.adapter = rankAdapter

        // District
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, PoliceData.districts)
        spinnerDistrict.adapter = districtAdapter

        // Station (Dependent on District)
        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDistrict = PoliceData.districts[position]
                val stations = PoliceData.getStationsForDistrict(selectedDistrict)
                val stationAdapter = ArrayAdapter(this@SignupActivity, android.R.layout.simple_spinner_dropdown_item, stations)
                spinnerStation.adapter = stationAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDatePickers() {
        etDob.setOnClickListener { showDatePicker(etDob) }
        etJoiningDate.setOnClickListener { showDatePicker(etJoiningDate) }
    }

    private fun showDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = "$day-${month + 1}-$year"
                editText.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val mobile = etMobile.text.toString().trim()
        val dob = etDob.text.toString().trim()
        val buckleNo = etBuckleNo.text.toString().trim()
        val joiningDate = etJoiningDate.text.toString().trim()
        val rank = spinnerRank.selectedItem?.toString() ?: ""
        val district = spinnerDistrict.selectedItem?.toString() ?: ""
        val station = spinnerStation.selectedItem?.toString() ?: ""
        val bloodGroup = spinnerBloodGroup.selectedItem?.toString() ?: ""

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || mobile.isEmpty() || buckleNo.isEmpty()) {
            Toast.makeText(this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show()
            return
        }

        btnRegister.isEnabled = false
        btnRegister.text = "Registering..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val userId = firebaseUser?.uid ?: ""

                    val newUser = User(
                        userId = userId,
                        fullName = name,
                        email = email,
                        mobileNumber = mobile,
                        dob = dob,
                        bloodGroup = bloodGroup,
                        buckleNumber = buckleNo,
                        rank = rank,
                        joiningDate = joiningDate,
                        district = district,
                        policeStation = station
                    )

                    saveUserToDb(newUser)
                } else {
                    btnRegister.isEnabled = true
                    btnRegister.text = "REGISTER OFFICER"
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToDb(user: User) {
        // 1. Save to Firebase Realtime Database
        database.getReference("users").child(user.userId).setValue(user)
            .addOnSuccessListener {
                // 2. Save to Local Room DB
                val db = AppDatabase.getInstance(this)
                lifecycleScope.launch(Dispatchers.IO) {
                    db.userDao().insertUser(user)
                    
                    // Save minimal session info if needed
                    val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
                    prefs.edit().putString("officer_id", user.buckleNumber).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignupActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignupActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    }
                }
            }
            .addOnFailureListener { e ->
                btnRegister.isEnabled = true
                btnRegister.text = "REGISTER OFFICER"
                Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
