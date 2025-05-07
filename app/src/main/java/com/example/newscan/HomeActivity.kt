package com.example.newscan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val btnUpdateProfile = findViewById<Button>(R.id.btnUpdateProfile)
        val btnScanBarcode = findViewById<Button>(R.id.btnScanBarcode)
        val lottieAnimation = findViewById<LottieAnimationView>(R.id.lottieAnimation)

        // Start Lottie animation
        lottieAnimation.playAnimation()

        // Profile button
        btnUpdateProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Scan barcode button
        btnScanBarcode.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Button animations
        btnUpdateProfile.animate().scaleX(1.1f).scaleY(1.1f).setDuration(1000).withEndAction {
            btnUpdateProfile.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000).start()
        }.start()

        btnScanBarcode.animate().scaleX(1.1f).scaleY(1.1f).setDuration(1000).withEndAction {
            btnScanBarcode.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000).start()
        }.start()
    }
}