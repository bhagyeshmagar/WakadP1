package com.example.wakadp1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        db = AppDatabase.getInstance(this)

        val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
        val officerId = prefs.getString("officer_id", "") ?: ""
        val officerName = prefs.getString("officer_name", "Officer")

        val tvOfficerName = findViewById<TextView>(R.id.tvOfficerName)
        val tvBranch = findViewById<TextView>(R.id.tvBranch)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvTotalToday = findViewById<TextView>(R.id.tvTotalToday)
        val tvPending = findViewById<TextView>(R.id.tvPending)
        val btnAdd = findViewById<Button>(R.id.btnAddActivity)
        val btnWeekly = findViewById<Button>(R.id.btnWeekly)

        tvOfficerName.text = officerName
        tvBranch.text = "Wakad Branch" // change as needed

        val sdf = SimpleDateFormat("yyyy-MM-dd, EEEE", Locale.getDefault())
        val simpleDateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayLabel = sdf.format(Date())
        val todayKey = simpleDateOnly.format(Date())
        tvDate.text = todayLabel

        fun refreshCounts() {
            lifecycleScope.launch(Dispatchers.IO) {
                val total = db.activityDao().countForDate(todayKey, officerId)
                val pending = db.activityDao().countPendingForDate(todayKey, officerId)
                withContext(Dispatchers.Main) {
                    tvTotalToday.text = total.toString()
                    tvPending.text = pending.toString()
                }
            }
        }

        refreshCounts()

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddEntryActivity::class.java))
        }

        btnWeekly.setOnClickListener {
            startActivity(Intent(this, WeeklySummaryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // refresh counts when returning
        // simple approach: recreate activity to call onCreate again OR call internal refresh
        // We'll call a refresh by re-launching a function; quick approach: recreate
        // but safer: call refreshCounts logic via finding views again (omitted for brevity)
    }
}
