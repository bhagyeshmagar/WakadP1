package com.example.wakadp1.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.wakadp1.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * SyncWorker - Background worker that syncs local activity entries to Firebase.
 * 
 * This worker runs when:
 * 1. Network becomes available after being offline
 * 2. Periodically to ensure data consistency
 * 3. Right after adding a new entry (one-time work)
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "SyncWorker"
        const val WORK_NAME = "activity_sync_work"
    }

    override suspend fun doWork(): Result {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Log.w(TAG, "No authenticated user, skipping sync")
                return Result.success()
            }

            val db = AppDatabase.getInstance(applicationContext)
            val entries = db.activityDao().getAllActivitiesSync()

            if (entries.isEmpty()) {
                Log.d(TAG, "No entries to sync")
                return Result.success()
            }

            // Sync each entry to Firebase
            val firebaseRef = FirebaseDatabase.getInstance()
                .getReference("activities")
                .child(uid)

            entries.forEach { entry ->
                try {
                    firebaseRef.child(entry.id.toString()).setValue(entry).await()
                    Log.d(TAG, "Synced entry: ${entry.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync entry ${entry.id}: ${e.message}")
                }
            }

            Log.i(TAG, "Sync completed: ${entries.size} entries")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed: ${e.message}")
            Result.retry()
        }
    }
}
