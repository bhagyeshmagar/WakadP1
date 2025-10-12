package com.example.wakadp1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakadp1.adapters.WeeklyAdapter
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.WeekSummary
import com.example.wakadp1.data.DaySummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeeklySummaryActivity : AppCompatActivity() {

    private lateinit var recyclerWeeks: androidx.recyclerview.widget.RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_summary)

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
}
