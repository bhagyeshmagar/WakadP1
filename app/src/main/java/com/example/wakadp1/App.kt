package com.example.wakadp1

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.wakadp1.data.AppDatabase
import com.example.wakadp1.workers.SyncWorker
import java.util.concurrent.TimeUnit

class App : Application() {

    companion object {
        private const val TAG = "WakadP1App"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Room Database
        AppDatabase.getInstance(this)
        
        // Initialize WorkManager for periodic background sync
        initializeWorkManager()
        
        Log.i(TAG, "Application initialized with advanced features")
    }

    private fun initializeWorkManager() {
        // Constraints: Only sync when connected to network
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Periodic sync every 6 hours (minimum 15 minutes for PeriodicWork)
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // Enqueue unique work - replaces if exists
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        Log.d(TAG, "WorkManager initialized for periodic sync")
    }
}
