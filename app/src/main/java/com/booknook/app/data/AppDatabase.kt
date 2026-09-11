package com.booknook.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromBookStatus(status: BookStatus): String = status.name

    @TypeConverter
    fun toBookStatus(value: String): BookStatus = BookStatus.valueOf(value)

    @TypeConverter
    fun fromNoteType(type: NoteType): String = type.name

    @TypeConverter
    fun toNoteType(value: String): NoteType = NoteType.valueOf(value)
}

@Database(
    entities = [Book::class, Note::class, ReadingLog::class, Badge::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun noteDao(): NoteDao
    abstract fun readingLogDao(): ReadingLogDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "booknook.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
