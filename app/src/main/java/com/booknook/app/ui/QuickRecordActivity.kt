package com.booknook.app.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.booknook.app.R
import com.booknook.app.data.AppDatabase
import com.booknook.app.data.Book
import com.booknook.app.data.BookStatus
import com.booknook.app.data.LocalBookRepository
import com.booknook.app.data.ReadingLog
import com.booknook.app.databinding.ActivityQuickRecordBinding
import kotlinx.coroutines.launch

/** 진행률/페이지 수만 입력하고 바로 저장하는 화면. 읽는 중인 책이 정해져 있어야 진입 가능. */
class QuickRecordActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }

    private lateinit var binding: ActivityQuickRecordBinding
    private lateinit var repository: LocalBookRepository
    private var book: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = intent.getIntExtra(EXTRA_BOOK_ID, -1)
        if (bookId == -1) {
            finish()
            return
        }

        repository = LocalBookRepository(AppDatabase.getInstance(applicationContext))

        binding.progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.progressValueText.text = getString(R.string.quick_record_progress_format, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.saveButton.setOnClickListener { saveRecord() }

        loadBook(bookId)
    }

    private fun loadBook(bookId: Int) {
        lifecycleScope.launch {
            val loaded = repository.getBook(bookId)
            if (loaded == null) {
                finish()
                return@launch
            }
            book = loaded
            binding.bookTitleText.text = getString(R.string.quick_record_title_format, loaded.title)
            binding.progressSeekBar.progress = loaded.progressPercent
            binding.progressValueText.text =
                getString(R.string.quick_record_progress_format, loaded.progressPercent)
        }
    }

    private fun saveRecord() {
        val current = book ?: return
        val newProgress = binding.progressSeekBar.progress
        val pagesRead = binding.pagesInput.text?.toString()?.trim()?.toIntOrNull()

        lifecycleScope.launch {
            val now = System.currentTimeMillis()
            val justCompleted = newProgress >= 100 && current.status != BookStatus.COMPLETED

            val updated = current.copy(
                progressPercent = newProgress,
                status = when {
                    justCompleted -> BookStatus.COMPLETED
                    current.status == BookStatus.WANT_TO_READ -> BookStatus.READING
                    else -> current.status
                },
                startDate = current.startDate ?: now,
                endDate = if (justCompleted) now else current.endDate
            )
            repository.saveBook(updated)
            repository.addReadingLog(ReadingLog(bookId = updated.id, date = now, pagesRead = pagesRead))

            Toast.makeText(
                this@QuickRecordActivity,
                getString(
                    if (justCompleted) R.string.quick_record_completed_toast
                    else R.string.quick_record_saved_toast
                ),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}
