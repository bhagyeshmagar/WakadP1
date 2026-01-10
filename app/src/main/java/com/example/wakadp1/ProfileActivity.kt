package com.example.wakadp1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etMobile: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etRank: TextInputEditText
    private lateinit var etBuckleNo: TextInputEditText
    private lateinit var etStation: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var fabEdit: FloatingActionButton
    private lateinit var tvHeaderName: TextView
    private lateinit var tvHeaderRank: TextView

    private var currentUser: User? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        initializeViews()
        loadUserData()

        fabEdit.setOnClickListener {
            toggleEditMode()
        }

        btnSave.setOnClickListener {
            saveProfileChanges()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etProfileName)
        etMobile = findViewById(R.id.etProfileMobile)
        etEmail = findViewById(R.id.etProfileEmail)
        etRank = findViewById(R.id.etProfileRank)
        etBuckleNo = findViewById(R.id.etProfileBuckleNo)
        etStation = findViewById(R.id.etProfileStation)
        btnSave = findViewById(R.id.btnSaveProfile)
        fabEdit = findViewById(R.id.fabEdit)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        tvHeaderRank = findViewById(R.id.tvHeaderRank)
    }

    private fun loadUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            
            // Try to get by Firebase UID first
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                currentUser = db.userDao().getUser(uid)
            }
            
            // Fallback to any user if specific not found (for single user device assumption)
            if (currentUser == null) {
                currentUser = db.userDao().getCurrentUser()
            }

            withContext(Dispatchers.Main) {
                if (currentUser != null) {
                    populateUI(currentUser!!)
                } else {
                    Toast.makeText(this@ProfileActivity, "User profile not found locally.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateUI(user: User) {
        etName.setText(user.fullName)
        etMobile.setText(user.mobileNumber)
        etEmail.setText(user.email)
        etRank.setText(user.rank)
        etBuckleNo.setText(user.buckleNumber)
        etStation.setText(user.policeStation)

        tvHeaderName.text = user.fullName
        tvHeaderRank.text = "${user.rank} | ${user.buckleNumber}"
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        
        val fields = listOf(etName, etMobile, etStation) // Allow editing Name, Mobile, Station. Rank/Buckle usually fixed.
        fields.forEach { it.isEnabled = isEditMode }

        if (isEditMode) {
            fabEdit.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            btnSave.visibility = android.view.View.VISIBLE
            etName.requestFocus()
        } else {
            fabEdit.setImageResource(android.R.drawable.ic_menu_edit)
            btnSave.visibility = android.view.View.GONE
            // Reset fields to last saved state if cancelled? Or just keep them.
            // keeping them is fine for simple implementation.
        }
    }

    private fun saveProfileChanges() {
        if (currentUser == null) return

        val newName = etName.text.toString().trim()
        val newMobile = etMobile.text.toString().trim()
        val newStation = etStation.text.toString().trim()

        if (newName.isEmpty() || newMobile.isEmpty()) {
            Toast.makeText(this, "Name and Mobile cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Create updated user object
        val updatedUser = currentUser!!.copy(
            fullName = newName,
            mobileNumber = newMobile,
            policeStation = newStation
        )

        lifecycleScope.launch(Dispatchers.IO) {
            // Update Local
            val db = AppDatabase.getInstance(applicationContext)
            db.userDao().updateUser(updatedUser)

            // Update Remote
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(updatedUser)
            }

            withContext(Dispatchers.Main) {
                currentUser = updatedUser
                populateUI(updatedUser)
                toggleEditMode() // Exit edit mode
                Toast.makeText(this@ProfileActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
