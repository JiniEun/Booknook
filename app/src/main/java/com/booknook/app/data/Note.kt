package com.booknook.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class NoteType {
    HIGHLIGHT,
    REVIEW,
    MEMO
}

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val type: NoteType,
    val content: String,
    val pageNumber: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
