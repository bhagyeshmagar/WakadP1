package com.example.wakadp1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

<<<<<<< HEAD
@Database(entities = [ActivityEntry::class], version = 1)
=======
@Database(entities = [ActivityEntry::class], version = 2, exportSchema = false)
>>>>>>> 30baf9b (Initial commit)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
<<<<<<< HEAD
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "waka_db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
=======
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waka_police_db"
                )
                    .fallbackToDestructiveMigration() // 💥 ensures schema recreates on change
                    .build()
                INSTANCE = instance
                instance
>>>>>>> 30baf9b (Initial commit)
            }
        }
    }
}
