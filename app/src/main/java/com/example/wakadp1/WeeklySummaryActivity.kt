package com.example.wakadp1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wakadp1.adapters.WeeklyAdapter
import com.example.wakadp1.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import com.example.wakadp1.data.WeekSummary
import com.example.wakadp1.data.DaySummary
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

            if (entries.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WeeklySummaryActivity, "No entries found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val weeks = groupByWeeks(entries)
            val sortedWeeks = weeks.sortedByDescending { it.weekRange.substring(0, 10) }

            withContext(Dispatchers.Main) {
                recyclerWeeks.adapter = WeeklyAdapter(sortedWeeks)
            }
        }
    }

    private fun groupByWeeks(entries: List<com.example.wakadp1.data.ActivityEntry>): List<WeekSummary> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
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
                val formatted = try { displayFormat.format(format.parse(date)!!) } catch (e: Exception) { date }
                DaySummary(
                    date = date,
                    dateFormatted = formatted,
                    count = entriesForDay.size,
                    entries = entriesForDay
                )
            }.sortedByDescending { it.date }

            WeekSummary("$start - $end", daysGrouped)
        }
    }

}
