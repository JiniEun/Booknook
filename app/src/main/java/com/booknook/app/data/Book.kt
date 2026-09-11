package com.booknook.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookStatus {
    WANT_TO_READ,
    READING,
    COMPLETED,
    PAUSED
}

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String? = null,
    val coverPath: String? = null,
    val status: BookStatus = BookStatus.WANT_TO_READ,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val progressPercent: Int = 0,
    val rating: Int? = null,
    val tags: String? = null, // 콤마 구분 문자열 (MVP)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
