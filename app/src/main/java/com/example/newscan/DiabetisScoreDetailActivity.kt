package com.example.newscan

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DiabetisScoreDetailActivity : AppCompatActivity() {

    private lateinit var Score : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_diabetis_score_detail)
        Score = findViewById(R.id.Score)
        val score = intent.getDoubleExtra("SCORE", 0.0)
        Score.text = "Your Score is $score"
    }
}