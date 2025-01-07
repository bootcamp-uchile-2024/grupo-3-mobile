package com.cotiledon.mobilApp.ui.activities

import android.content.Intent
import android.os.Bundle
import com.cotiledon.mobilApp.R
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.cotiledon.mobilApp.ui.fragments.SignInFragment


class MainActivity : BaseActivity() {

    private lateinit var progressBar: ProgressBar
    private val splashDuration: Long = 1500 //1.5 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        progressBar = findViewById(R.id.progressBar)

        animateProgressBar()

        Handler(Looper.getMainLooper()).postDelayed({
            startMainApp()
        }, splashDuration)
    }

    private fun animateProgressBar() {
        val progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        progressAnimator.apply {
            duration = splashDuration
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun startMainApp() {
        val intent = Intent(this, MainContainerActivity::class.java)
        startActivity(intent)
        finish()
    }
}