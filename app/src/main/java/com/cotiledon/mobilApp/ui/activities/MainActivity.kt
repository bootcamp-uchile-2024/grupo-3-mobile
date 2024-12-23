package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import com.cotiledon.mobilApp.R
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private val splashDuration: Long = 1500 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the action bar
        supportActionBar?.hide()

        // Initialize progress bar
        progressBar = findViewById(R.id.progressBar)

        // Animate progress bar
        animateProgressBar()

        // Navigate to main app after splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            startMainApp()
        }, splashDuration)
    }

    private fun animateProgressBar() {
        // Create progress animation
        val progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        progressAnimator.apply {
            duration = splashDuration
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun startMainApp() {
        // Navigate to main container activity
        val intent = Intent(this, MainContainerActivity::class.java)
        startActivity(intent)
        finish() // Close splash screen so it's not in the back stack
    }
}