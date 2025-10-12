package com.example.wakadp1.data

import androidx.room.ColumnInfo

data class ActivityTypeCount(
    @ColumnInfo(name = "activityType")
    val type: String,
    val count: Int
)
