package com.bagel.noink.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bagel.noink.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarLayout.toolbar)

        val backButton = binding.appBarLayout.back
        backButton.setOnClickListener {
            onBackPressed()
        }

        val searchButton = binding.appBarLayout.search
        searchButton.setOnClickListener {
            val intent = Intent(this@HistoryActivity, SearchActivity::class.java)
            startActivity(intent)
        }
    }
}