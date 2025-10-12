package com.example.wakadp1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wakadp1.data.ActivityEntry
import com.example.wakadp1.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddEntryActivity : AppCompatActivity() {

    private var attachUri: Uri? = null
    private var startMillis = 0L
    private var endMillis = 0L
    private var selectedDateMillis = 0L

    private lateinit var spinner: Spinner
    private lateinit var tvDate: TextView
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var etNotes: EditText
    private lateinit var chkPending: CheckBox
    private lateinit var btnAttach: Button
    private lateinit var btnSave: Button

    // File picker launcher
    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            attachUri = uri
            if (uri != null) {
                Toast.makeText(this, "Attached: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        // Initialize views
        spinner = findViewById(R.id.spinnerType)
        tvDate = findViewById(R.id.tvDate)
        tvStart = findViewById(R.id.tvStartTime)
        tvEnd = findViewById(R.id.tvEndTime)
        etNotes = findViewById(R.id.etNotes)
        chkPending = findViewById(R.id.chkPending)
        btnAttach = findViewById(R.id.btnAttach)
        btnSave = findViewById(R.id.btnSave)

        // Load spinner data
        val arr = resources.getStringArray(R.array.activity_types)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arr)

        // --- DATE PICKER ---
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Set current date by default
        selectedDateMillis = calendar.timeInMillis
        tvDate.text = dateFormat.format(calendar.time)

        tvDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDateMillis = calendar.timeInMillis
                    tvDate.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Time picker handlers
        tvStart.setOnClickListener { showTimePicker(true) }
        tvEnd.setOnClickListener { showTimePicker(false) }

        // File picker
        btnAttach.setOnClickListener {
            pickFileLauncher.launch("*/*")
        }

        // Save handler
        btnSave.setOnClickListener {
            saveEntry()
        }
    }

    private fun showTimePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)

            val formatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
            if (isStart) {
                startMillis = cal.timeInMillis
                tvStart.text = formatted
            } else {
                endMillis = cal.timeInMillis
                tvEnd.text = formatted
            }
        }

        TimePickerDialog(
            this,
            timeListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun saveEntry() {
        val selectedType = spinner.selectedItem?.toString() ?: ""
        val notes = etNotes.text?.toString() ?: ""
        val isPending = chkPending.isChecked

        if (selectedDateMillis == 0L) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (startMillis == 0L || endMillis == 0L) {
            Toast.makeText(this, "Please select start and end times", Toast.LENGTH_SHORT).show()
            return
        }

        if (endMillis <= startMillis) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
        val officerId = prefs.getString("officer_id", "") ?: ""
        val branch = "Wakad Branch"

        // Format date for DB
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDateMillis))

        val entry = ActivityEntry(
            officerId = officerId,
            branchName = branch,
            activityType = selectedType,
            startTime = startMillis,
            endTime = endMillis,
            notes = notes,
            attachUri = attachUri?.toString(),
            date = dateKey,
            isPending = isPending
        )

        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            db.activityDao().insert(entry)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddEntryActivity, "Entry saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
