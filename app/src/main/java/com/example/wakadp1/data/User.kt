package com.example.wakadp1.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String = "", // Firebase UID
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val dob: String = "",
    val bloodGroup: String = "",
    
    // Service Details
    val buckleNumber: String = "",
    val rank: String = "",
    val joiningDate: String = "",
    
    // Posting Details
    val district: String = "",
    val policeStation: String = "",
    
    val profileImageUrl: String = "" // For future use
)
