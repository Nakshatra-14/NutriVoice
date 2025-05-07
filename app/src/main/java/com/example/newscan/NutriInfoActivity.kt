package com.example.newscan

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NutriInfoActivity: AppCompatActivity() {
    private lateinit var Score : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_info)
        Score = findViewById(R.id.Score)
        val score = intent.getStringExtra("NUTRI-SCORE")
        if (score != null) {
            Score.text = "\n\nYour Nutri-Score is: ${score.toUpperCase()}"
        }
    }
}