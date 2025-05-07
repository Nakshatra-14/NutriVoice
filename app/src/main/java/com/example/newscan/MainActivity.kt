package com.example.newscan

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var codeScanner: CodeScanner
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var teamInfo: TextView
    private lateinit var manualInput: TextView
    private lateinit var textToSpeech: TextToSpeech
    private var isTtsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        teamInfo = findViewById(R.id.teamInfo)
        manualInput = findViewById(R.id.instruction_text)
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)

        // Scanner setup
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ONE_DIMENSIONAL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // Decode callback
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                mediaPlayer.start()
                Toast.makeText(this, "Barcode scanned", Toast.LENGTH_LONG).show()

                // Start DetailActivity and pass the scanned code
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra("BARCODE", it.text)
                startActivity(intent)
            }
        }

        manualInput.setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeInputActivity::class.java)
            startActivity(intent)
        }

        teamInfo.setOnClickListener {
            val intent = Intent(this@MainActivity, TeamInfoActivity::class.java)
            startActivity(intent)
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        checkPermission(android.Manifest.permission.CAMERA, 200)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to English (default)
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "English language not supported for TTS", Toast.LENGTH_SHORT).show()
            } else {
                // Configure TTS
                textToSpeech.setPitch(1.0f)
                textToSpeech.setSpeechRate(0.9f)
                isTtsInitialized = true

                // Play welcome message
                val welcomeMessage = "please scan barcode or use manual input to continue"
                textToSpeech.speak(welcomeMessage, TextToSpeech.QUEUE_FLUSH, null, "WelcomeUtterance")
            }
        } else {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
            isTtsInitialized = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start the camera preview
                codeScanner.startPreview()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    private fun checkPermission(permission: String, reqCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), reqCode)
        }
    }
}