package com.example.wakadp1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityEntry)

    @Query("SELECT * FROM activity_entries ORDER BY date DESC, startTime ASC")
    suspend fun getAllEntries(): List<ActivityEntry>

    @Query("SELECT * FROM activity_entries WHERE date = :date ORDER BY startTime ASC")
    suspend fun getEntriesByDate(date: String): List<ActivityEntry>

    @Query("SELECT COUNT(*) FROM activity_entries WHERE officerId = :officerId AND date = :date")
    suspend fun countForDate(officerId: String, date: String): Int

    @Query("SELECT COUNT(*) FROM activity_entries WHERE officerId = :officerId AND date = :date AND isPending = 1")
    suspend fun countPendingForDate(officerId: String, date: String): Int

    @Query("""
        SELECT * FROM activity_entries
        WHERE officerId = :officerId
          AND activityType = :type
          AND date BETWEEN :startDate AND :endDate
        ORDER BY date ASC, startTime ASC
    """)
    suspend fun entriesByTypeAndRange(
        officerId: String,
        type: String,
        startDate: String,
        endDate: String
    ): List<ActivityEntry>

    @Query("""
        SELECT * FROM activity_entries
        WHERE officerId = :officerId
          AND date BETWEEN :startDate AND :endDate
        ORDER BY date ASC, startTime ASC
    """)
    suspend fun entriesByDateRange(
        officerId: String,
        startDate: String,
        endDate: String
    ): List<ActivityEntry>

    @Query("""
        SELECT activityType, COUNT(*) AS count
        FROM activity_entries
        WHERE officerId = :officerId
          AND date BETWEEN :startDate AND :endDate
        GROUP BY activityType
    """)
    suspend fun countsByType(
        officerId: String,
        startDate: String,
        endDate: String
    ): List<ActivityTypeCount>

    @Query("DELETE FROM activity_entries")
    suspend fun clearAll()
}
