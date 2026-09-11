package com.booknook.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String, // 예: "FIRST_COMPLETE", "STREAK_7", "MONTHLY_5"
    val earnedAt: Long = System.currentTimeMillis()
)
