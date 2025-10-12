package com.example.wakadp1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.ActivityTypeCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeeklySummaryActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: SummaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_summary)

        rv = findViewById(R.id.rvSummary)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = SummaryAdapter { type ->
            // open details
            val i = Intent(this, ActivityDetailsActivity::class.java)
            i.putExtra("type", type)
            startActivity(i)
        }
        rv.adapter = adapter

        loadWeeklyCounts()
    }

    private fun loadWeeklyCounts() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            // compute week range (Mon-Sun) - simple approach: last 7 days
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val to = Date()
            val cal = Calendar.getInstance()
            cal.time = to
            val toKey = sdf.format(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, -6)
            val fromKey = sdf.format(cal.time)

            val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
            val officerId = prefs.getString("officer_id", "") ?: ""

            val counts: List<ActivityTypeCount> = db.activityDao().countsByType(fromKey, toKey, officerId)
            withContext(Dispatchers.Main) {
                adapter.submitList(counts)
            }
        }
    }
}
