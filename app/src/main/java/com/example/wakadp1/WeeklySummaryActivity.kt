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
        setupExportButton()
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
                
                // Populate Chart
                val chartData = entries.groupBy { it.activityType }
                    .map { (type, list) -> type to list.size }
                findViewById<BarChartView>(R.id.barChart).setData(chartData)
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



    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    // Export PDF Feature
    private fun setupExportButton() {
        findViewById<android.view.View>(R.id.btnExport).setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(android.content.Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(android.content.Intent.EXTRA_TITLE, "Police_Report_${System.currentTimeMillis()}.pdf")
            }
            startActivityForResult(intent, 1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                generatePdf(uri)
            }
        }
    }

    private fun generatePdf(uri: android.net.Uri) {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()

        // Draw basic content
        paint.color = android.graphics.Color.BLACK
        paint.textSize = 14f
        canvas.drawText("Wakad Police Station - Weekly Report", 50f, 50f, paint)
        
        // Simple iteration to draw text just to prove concept (full view drawing is complex)
        // In a real app, we would measure and draw the recyclerview content or specific report data
        paint.textSize = 12f
        var y = 100f
        canvas.drawText("Generated on: ${java.util.Date()}", 50f, 80f, paint)
        
        pdfDocument.finishPage(page)

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(this, "PDF Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
