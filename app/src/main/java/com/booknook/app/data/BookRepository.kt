package com.booknook.app.data

import kotlinx.coroutines.flow.Flow

/**
 * 데이터 계층 추상화.
 * 지금은 LocalRepository만 존재하지만, 나중에 CloudSyncRepository를 추가해
 * 이 인터페이스를 그대로 구현하는 방식으로 갈아 끼울 수 있음 (로컬은 항상 source of truth).
 */
interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>>
    suspend fun getBook(id: Int): Book?
    suspend fun saveBook(book: Book): Long
    suspend fun deleteBook(book: Book)

    fun getNotesForBook(bookId: Int): Flow<List<Note>>
    suspend fun saveNote(note: Note): Long

    fun getReadingLogs(): Flow<List<ReadingLog>>
    suspend fun addReadingLog(log: ReadingLog): Long

    fun getBadges(): Flow<List<Badge>>
    suspend fun awardBadge(code: String)
}

class LocalBookRepository(private val db: AppDatabase) : BookRepository {

    override fun getAllBooks(): Flow<List<Book>> = db.bookDao().getAll()

    override fun getBooksByStatus(status: BookStatus): Flow<List<Book>> =
        db.bookDao().getByStatus(status)

    override suspend fun getBook(id: Int): Book? = db.bookDao().getById(id)

    override suspend fun saveBook(book: Book): Long =
        db.bookDao().upsert(book.copy(updatedAt = System.currentTimeMillis()))

    override suspend fun deleteBook(book: Book) = db.bookDao().delete(book)

    override fun getNotesForBook(bookId: Int): Flow<List<Note>> =
        db.noteDao().getByBook(bookId)

    override suspend fun saveNote(note: Note): Long = db.noteDao().upsert(note)

    override fun getReadingLogs(): Flow<List<ReadingLog>> = db.readingLogDao().getAll()

    override suspend fun addReadingLog(log: ReadingLog): Long =
        db.readingLogDao().upsert(log)

    override fun getBadges(): Flow<List<Badge>> = db.badgeDao().getAll()

    override suspend fun awardBadge(code: String) {
        db.badgeDao().insert(Badge(code = code))
    }
}
