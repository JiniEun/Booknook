package com.booknook.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.booknook.app.data.AppDatabase
import com.booknook.app.data.LocalBookRepository
import com.booknook.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(applicationContext)
        val repository = LocalBookRepository(db)

        // TODO: 홈 화면 구성 - 오늘의 기록 버튼, 읽는 중인 책, 스트릭 표시
        binding.appTitle.text = getString(com.booknook.app.R.string.app_name)
    }
}
