package com.example.wakadp1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE userId = :uid LIMIT 1")
    suspend fun getUser(uid: String): User?

    @Query("SELECT * FROM users LIMIT 1") // Helper to get current logged in user if single user app
    suspend fun getCurrentUser(): User?
}
