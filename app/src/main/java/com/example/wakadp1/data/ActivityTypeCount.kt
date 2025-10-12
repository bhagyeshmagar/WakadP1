package com.example.wakadp1.data

<<<<<<< HEAD
data class ActivityTypeCount(
    val activityType: String,
=======
import androidx.room.ColumnInfo

data class ActivityTypeCount(
    @ColumnInfo(name = "activityType")
    val type: String,
>>>>>>> 30baf9b (Initial commit)
    val count: Int
)
