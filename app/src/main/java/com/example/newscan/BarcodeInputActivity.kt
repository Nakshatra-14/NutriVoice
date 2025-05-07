package com.example.newscan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class BarcodeInputActivity : AppCompatActivity() {
    private lateinit var input : EditText
    private lateinit var output : TextView
    private lateinit var submit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_barcode_input)

        input = findViewById(R.id.input)
        submit = findViewById(R.id.button)
        output = findViewById(R.id.output)
        var barcode : String
        submit.setOnClickListener {
            barcode = input.text.toString()

            if(barcode.isEmpty()){
                output.text = "Please enter a barcode"
                return@setOnClickListener
            }
//            else if(barcode.length != 13)
//                output.text = "Invalid barcode"
            else {
                output.text = barcode

                val intent = Intent(this@BarcodeInputActivity, DetailActivity::class.java)
                intent.putExtra("BARCODE", barcode)
                startActivity(intent)
            }
        }
    }
}