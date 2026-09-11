package com.booknook.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_logs",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReadingLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val date: Long,
    val pagesRead: Int? = null,
    val memo: String? = null
)
