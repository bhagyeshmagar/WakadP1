package com.example.wakadp1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.adapters.EntryAdapter
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.ActivityEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ActivityDetailsActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private var adapter: EntryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_summary) // reuse layout

        // Label setup
        val tvWeekLabel = findViewById<TextView>(R.id.tvWeekLabel)
        tvWeekLabel.text = "Activity Details"

        // Recycler setup
        rv = findViewById(R.id.rvSummary)
        rv.layoutManager = LinearLayoutManager(this)

        val type = intent.getStringExtra("type") ?: return
        loadEntries(type)
    }

    private fun loadEntries(type: String) {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val to = Date()
            val cal = Calendar.getInstance()
            cal.time = to
            val toKey = sdf.format(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, -6)
            val fromKey = sdf.format(cal.time)

            val prefs = getSharedPreferences("waka_prefs", MODE_PRIVATE)
            val officerId = prefs.getString("officer_id", "") ?: ""

            val entries: List<ActivityEntry> =
                db.activityDao().entriesByTypeAndRange(fromKey, toKey, officerId, type)

            withContext(Dispatchers.Main) {
                adapter = EntryAdapter(entries)
                rv.adapter = adapter
            }
        }
    }
}

