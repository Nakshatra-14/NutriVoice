package com.example.newscan

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ViewImgActivity : AppCompatActivity() {
    private lateinit var productView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_img)

        val Url = intent.getStringExtra("IMG-URL")
        productView = findViewById(R.id.productView)
        // Load the image using Glide
        Glide.with(this)
            .load(Url)
            .into(productView)

    }
}