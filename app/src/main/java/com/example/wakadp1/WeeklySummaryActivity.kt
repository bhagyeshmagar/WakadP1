package com.example.wakadp1

<<<<<<< HEAD
import android.content.Intent
=======
>>>>>>> 30baf9b (Initial commit)
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
<<<<<<< HEAD
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.ActivityTypeCount
=======
import com.example.wakadp1.adapters.WeeklyAdapter
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.WeekSummary
import com.example.wakadp1.data.DaySummary
>>>>>>> 30baf9b (Initial commit)
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeeklySummaryActivity : AppCompatActivity() {

<<<<<<< HEAD
    private lateinit var rv: RecyclerView
    private lateinit var adapter: SummaryAdapter
=======
    private lateinit var recyclerWeeks: androidx.recyclerview.widget.RecyclerView
>>>>>>> 30baf9b (Initial commit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_summary)

<<<<<<< HEAD
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
=======
        recyclerWeeks = findViewById(R.id.recyclerWeeks)
        recyclerWeeks.layoutManager = LinearLayoutManager(this)

        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val entries = db.activityDao().getAllEntries()
            val weeks = groupByWeeks(entries)
            withContext(Dispatchers.Main) {
                recyclerWeeks.adapter = WeeklyAdapter(weeks)
            }
        }
    }

    private fun groupByWeeks(entries: List<com.example.wakadp1.data.ActivityEntry>): List<WeekSummary> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val map = linkedMapOf<String, MutableList<com.example.wakadp1.data.ActivityEntry>>()

        for (entry in entries) {
            val date = format.parse(entry.date)
            val cal = Calendar.getInstance().apply { time = date!! }
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val start = format.format(cal.time)
            cal.add(Calendar.DAY_OF_WEEK, 6)
            val end = format.format(cal.time)
            val key = "$start|$end"

            map.getOrPut(key) { mutableListOf() }.add(entry)
        }

        return map.map { (range, list) ->
            val (start, end) = range.split("|")
            val daysGrouped = list.groupBy { it.date }.map { (date, entriesForDay) ->
                DaySummary(date, entriesForDay.size)
            }
            WeekSummary("$start - $end", daysGrouped)
        }
    }
>>>>>>> 30baf9b (Initial commit)
}
