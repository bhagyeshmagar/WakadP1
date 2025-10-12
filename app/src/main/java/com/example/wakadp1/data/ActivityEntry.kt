package com.example.wakadp1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val officerId: String,
    val branchName: String? = null,
    val activityType: String,
    val startTime: Long,
    val endTime: Long,
    val notes: String? = null,
    val attachUri: String? = null,
    val date: String, // yyyy-MM-dd
    val isPending: Boolean = false
)

