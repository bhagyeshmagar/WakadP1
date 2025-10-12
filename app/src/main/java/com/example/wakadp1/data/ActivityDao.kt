package com.example.wakadp1.data

import androidx.room.Dao
import androidx.room.Insert
<<<<<<< HEAD
=======
import androidx.room.OnConflictStrategy
>>>>>>> 30baf9b (Initial commit)
import androidx.room.Query

@Dao
interface ActivityDao {
<<<<<<< HEAD
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
=======

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
>>>>>>> 30baf9b (Initial commit)
}
