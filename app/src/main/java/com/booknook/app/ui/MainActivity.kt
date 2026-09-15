package com.booknook.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.booknook.app.R
import com.booknook.app.data.AppDatabase
import com.booknook.app.data.Book
import com.booknook.app.data.BookStatus
import com.booknook.app.data.LocalBookRepository
import com.booknook.app.data.ReadingLog
import com.booknook.app.databinding.ActivityMainBinding
import java.util.Calendar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: LocalBookRepository
    private var currentBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(applicationContext)
        repository = LocalBookRepository(db)

        binding.quickRecordButton.setOnClickListener {
            val book = currentBook
            if (book != null) {
                startActivity(Intent(this, QuickRecordActivity::class.java).apply {
                    putExtra(QuickRecordActivity.EXTRA_BOOK_ID, book.id)
                })
            } else {
                Toast.makeText(this, getString(R.string.home_no_current_book), Toast.LENGTH_SHORT).show()
            }
        }

        observeCurrentBook()
        observeStreak()
    }

    private fun observeCurrentBook() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getBooksByStatus(BookStatus.READING).collect { books ->
                    bindCurrentBook(books.firstOrNull())
                }
            }
        }
    }

    private fun bindCurrentBook(book: Book?) {
        currentBook = book
        if (book == null) {
            binding.currentBookCard.visibility = View.GONE
            binding.emptyBookText.visibility = View.VISIBLE
            return
        }
        binding.currentBookCard.visibility = View.VISIBLE
        binding.emptyBookText.visibility = View.GONE
        binding.currentBookTitle.text = book.title
        binding.currentBookAuthor.text = book.author ?: ""
        binding.currentBookAuthor.visibility =
            if (book.author.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.currentBookProgress.progress = book.progressPercent
        binding.currentBookProgressText.text =
            getString(R.string.home_progress_format, book.progressPercent)
    }

    private fun observeStreak() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getReadingLogs().collect { logs ->
                    val streak = calculateStreak(logs)
                    bindStreak(streak)
                }
            }
        }
    }

    private fun bindStreak(streak: Int) {
        binding.streakText.text = if (streak > 0) {
            getString(R.string.home_streak_format, streak)
        } else {
            getString(R.string.home_streak_zero)
        }
        binding.mascotMessage.text = getString(mascotMessageFor(streak))
    }

    private fun mascotMessageFor(streak: Int): Int = when {
        streak <= 0 -> R.string.home_mascot_message_streak_0
        streak < 3 -> R.string.home_mascot_message_streak_low
        streak < 7 -> R.string.home_mascot_message_streak_mid
        else -> R.string.home_mascot_message_streak_high
    }

    /**
     * 오늘 또는 어제까지 기록이 이어져 있어야 "살아있는" 스트릭으로 카운트.
     * 하루라도 비면 끊긴 것으로 보고 0.
     */
    private fun calculateStreak(logs: List<ReadingLog>): Int {
        if (logs.isEmpty()) return 0

        val oneDayMillis = 24 * 60 * 60 * 1000L
        val today = startOfDay(System.currentTimeMillis())
        val yesterday = today - oneDayMillis

        val distinctDays = logs.map { startOfDay(it.date) }.distinct().sortedDescending()
        if (distinctDays.isEmpty()) return 0
        if (distinctDays[0] != today && distinctDays[0] != yesterday) return 0

        var streak = 1
        var expected = distinctDays[0] - oneDayMillis
        for (i in 1 until distinctDays.size) {
            if (distinctDays[i] != expected) break
            streak++
            expected -= oneDayMillis
        }
        return streak
    }

    private fun startOfDay(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
