package com.example.wakadp1

import android.app.Application
import com.example.wakadp1.data.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(this)
    }
}
