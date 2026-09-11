package com.booknook.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE status = :status ORDER BY updatedAt DESC")
    fun getByStatus(status: BookStatus): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: Int): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getByBook(bookId: Int): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: Note): Long

    @Delete
    suspend fun delete(note: Note)
}

@Dao
interface ReadingLogDao {
    @Query("SELECT * FROM reading_logs WHERE bookId = :bookId ORDER BY date DESC")
    fun getByBook(bookId: Int): Flow<List<ReadingLog>>

    @Query("SELECT * FROM reading_logs ORDER BY date DESC")
    fun getAll(): Flow<List<ReadingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: ReadingLog): Long
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY earnedAt DESC")
    fun getAll(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: Badge): Long
}
