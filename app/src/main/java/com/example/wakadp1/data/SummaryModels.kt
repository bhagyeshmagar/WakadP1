package com.example.wakadp1.data

/**
 * Represents a single day's summary in a week.
 */
data class DaySummary(
    val date: String,
    val dateFormatted: String = date,
    val count: Int,
    val entries: List<ActivityEntry>
)

/**
 * Represents a week's summary with multiple days.
 */
data class WeekSummary(
    val weekRange: String, // Example: "2025-10-06 - 2025-10-12"
    val days: List<DaySummary>
)


