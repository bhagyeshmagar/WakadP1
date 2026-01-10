package com.example.wakadp1

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakadp1.adapters.WeeklyAdapter
import com.example.wakadp1.data.ActivityEntry
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.data.DaySummary
import com.example.wakadp1.data.WeekSummary
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WeeklySummaryActivity : AppCompatActivity() {

    private lateinit var recyclerWeeks: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabExport: ExtendedFloatingActionButton
    private var allEntries: List<ActivityEntry> = emptyList()
    private var officerName: String = "Officer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_summary)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerWeeks = findViewById(R.id.recyclerWeeks)
        recyclerWeeks.layoutManager = LinearLayoutManager(this)
        
        emptyState = findViewById(R.id.emptyState)
        fabExport = findViewById(R.id.fabExport)

        loadWeeklyData()
        setupExportButton()
    }

    private fun loadWeeklyData() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            allEntries = db.activityDao().getAllEntries()
            
            // Get officer name
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                db.userDao().getUser(uid)?.let {
                    officerName = it.fullName.ifEmpty { "Officer" }
                }
            }

            withContext(Dispatchers.Main) {
                if (allEntries.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    recyclerWeeks.visibility = View.GONE
                    fabExport.visibility = View.GONE
                    return@withContext
                }

                emptyState.visibility = View.GONE
                recyclerWeeks.visibility = View.VISIBLE
                fabExport.visibility = View.VISIBLE

                val weeks = groupByWeeks(allEntries)
                val sortedWeeks = weeks.sortedByDescending { it.weekRange.substring(0, 10) }
                recyclerWeeks.adapter = WeeklyAdapter(sortedWeeks)

                // Populate Chart (top 7 activities)
                val chartData = allEntries.groupBy { it.activityType }
                    .map { (type, list) -> type.take(15) to list.size }
                    .sortedByDescending { it.second }
                    .take(7)
                findViewById<BarChartView>(R.id.barChart).setData(chartData)
            }
        }
    }

    private fun groupByWeeks(entries: List<ActivityEntry>): List<WeekSummary> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        val map = linkedMapOf<String, MutableList<ActivityEntry>>()

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
                val formatted = try {
                    displayFormat.format(format.parse(date)!!)
                } catch (e: Exception) {
                    date
                }
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

    private fun setupExportButton() {
        // FAB export
        fabExport.setOnClickListener { exportPdf() }
        
        // Toolbar export icon
        findViewById<View>(R.id.btnExport).setOnClickListener { exportPdf() }
    }

    private fun exportPdf() {
        if (allEntries.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd_MMM_yyyy", Locale.getDefault())
        val filename = "Weekly_Report_${dateFormat.format(Date())}.pdf"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, filename)
        }
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            data?.data?.let { uri -> generatePdf(uri) }
        }
    }

    private fun generatePdf(uri: Uri) {
        val pdfDocument = PdfDocument()

        // A4 size in points: 595 x 842
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Paints
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            textAlign = Paint.Align.CENTER
        }

        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 11f
            isFakeBoldText = true
        }

        val headerBgPaint = Paint().apply {
            color = Color.parseColor("#4A4A6A")
            style = Paint.Style.FILL
        }

        val cellPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
        }

        val linePaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 1f
        }

        val signaturePaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }

        var y = margin

        // Title
        canvas.drawText("Weekly Activity Summary", pageWidth / 2f, y + 22f, titlePaint)
        y += 50f

        // Officer Info
        canvas.drawText("Officer Name: $officerName", pageWidth / 2f, y, subtitlePaint)
        y += 18f

        // Week Range
        val weekStart = allEntries.minByOrNull { it.date }?.date ?: ""
        val weekEnd = allEntries.maxByOrNull { it.date }?.date ?: ""
        canvas.drawText("Period: $weekStart to $weekEnd", pageWidth / 2f, y, subtitlePaint)
        y += 35f

        // Table dimensions
        val tableLeft = margin
        val tableWidth = pageWidth - 2 * margin
        val colWidths = floatArrayOf(55f, 140f, 70f, 70f, 140f)
        val rowHeight = 28f

        // Table Header Background
        canvas.drawRect(tableLeft, y, tableLeft + tableWidth, y + rowHeight, headerBgPaint)

        // Table Header Text
        val headers = arrayOf("Date", "Activity Type", "Start Time", "End Time", "Remarks")
        var x = tableLeft + 8f
        for (i in headers.indices) {
            canvas.drawText(headers[i], x, y + 18f, headerPaint)
            x += colWidths[i]
        }
        y += rowHeight

        // Table Rows
        val displayDateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for ((index, entry) in allEntries.take(20).withIndex()) {
            if (index % 2 == 0) {
                val altBgPaint = Paint().apply {
                    color = Color.parseColor("#F5F5F5")
                    style = Paint.Style.FILL
                }
                canvas.drawRect(tableLeft, y, tableLeft + tableWidth, y + rowHeight, altBgPaint)
            }

            x = tableLeft + 8f

            val dayAbbrev = try {
                val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(entry.date)
                displayDateFormat.format(parsedDate!!)
            } catch (e: Exception) {
                entry.date.takeLast(2)
            }
            canvas.drawText(dayAbbrev, x, y + 18f, cellPaint)
            x += colWidths[0]

            canvas.drawText(entry.activityType.take(22), x, y + 18f, cellPaint)
            x += colWidths[1]

            // Format times from Long (millis) to String
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val startTimeStr = timeFormatter.format(Date(entry.startTime))
            val endTimeStr = timeFormatter.format(Date(entry.endTime))

            canvas.drawText(startTimeStr, x, y + 18f, cellPaint)
            x += colWidths[2]

            canvas.drawText(endTimeStr, x, y + 18f, cellPaint)
            x += colWidths[3]

            val remarks = (entry.notes ?: "").take(20).ifEmpty { "-" }
            canvas.drawText(remarks, x, y + 18f, cellPaint)

            canvas.drawLine(tableLeft, y + rowHeight, tableLeft + tableWidth, y + rowHeight, linePaint)
            y += rowHeight
        }

        // Table border
        val tableBottom = y
        canvas.drawRect(tableLeft, margin + 85f, tableLeft + tableWidth, tableBottom, Paint().apply {
            style = Paint.Style.STROKE
            color = Color.GRAY
            strokeWidth = 1f
        })

        // Signature Section
        y += 50f
        canvas.drawText("Officer Signature: _______________________", tableLeft, y, signaturePaint)
        y += 25f
        canvas.drawText("Station Head Approval: _______________________", tableLeft, y, signaturePaint)
        y += 25f
        canvas.drawText("Date: _____________", tableLeft, y, signaturePaint)

        pdfDocument.finishPage(page)

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(this, "✓ PDF Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
