package com.bagel.noink.activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.bagel.noink.databinding.ActivityCommunityBinding


class CommunityActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCommunityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarLayout.toolbar)

        val backButton = binding.appBarLayout.back
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
