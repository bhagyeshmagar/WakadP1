package com.example.wakadp1.data

data class DaySummary(
    val dateFormatted: String,
    val count: Int
)

data class WeekSummary(
    val rangeText: String,
    val days: List<DaySummary>
)
