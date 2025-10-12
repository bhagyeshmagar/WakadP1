package com.example.wakadp1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(entry: ActivityEntry): Long

    @Query("SELECT COUNT(*) FROM activities WHERE date = :date AND officerId = :officerId")
    suspend fun countForDate(date: String, officerId: String): Int

    @Query("SELECT COUNT(*) FROM activities WHERE date = :date AND officerId = :officerId AND isPending = 1")
    suspend fun countPendingForDate(date: String, officerId: String): Int

    @Query("SELECT activityType AS activityType, COUNT(*) AS count FROM activities WHERE date BETWEEN :from AND :to AND officerId = :officerId GROUP BY activityType")
    suspend fun countsByType(from: String, to: String, officerId: String): List<ActivityTypeCount>

    @Query("SELECT * FROM activities WHERE date BETWEEN :from AND :to AND officerId = :officerId AND activityType = :type ORDER BY startTime ASC")
    suspend fun entriesByTypeAndRange(from: String, to: String, officerId: String, type: String): List<ActivityEntry>
}
