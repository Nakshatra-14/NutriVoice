package com.example.newscan

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.newscan.data.DatabaseModule
import com.example.newscan.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etAge)
        val spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        val etDiabetesValue = findViewById<EditText>(R.id.etDiabetesValue)
        val etAbout = findViewById<EditText>(R.id.etAbout)
        val etOtherInfo = findViewById<EditText>(R.id.etOtherInfo)
        val btnSubmitProfile = findViewById<Button>(R.id.btnSubmitProfile)

        // Setup gender spinner
        val genders = arrayOf("Male", "Female", "Other")
        spinnerGender.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)

        // Load existing profile (if any)
        CoroutineScope(Dispatchers.IO).launch {
            val db = DatabaseModule.provideDatabase(this@ProfileActivity)
            val profile = db.userProfileDao().getProfile()
            withContext(Dispatchers.Main) {
                profile?.let {
                    etName.setText(it.name)
                    etAge.setText(it.age.toString())
                    spinnerGender.setSelection(genders.indexOf(it.gender))
                    etDiabetesValue.setText(it.diabetesValue.toString())
                    etAbout.setText(it.about)
                    etOtherInfo.setText(it.otherInfo)
                }
            }
        }

        // Submit button
        btnSubmitProfile.setOnClickListener {
            val name = etName.text.toString().trim()
            val ageStr = etAge.text.toString().trim()
            val gender = spinnerGender.selectedItem.toString()
            val diabetesValueStr = etDiabetesValue.text.toString().trim()
            val about = etAbout.text.toString().trim()
            val otherInfo = etOtherInfo.text.toString().trim()

            if (name.isEmpty() || ageStr.isEmpty() || diabetesValueStr.isEmpty()) {
                Toast.makeText(this, "Please fill in Name, Age, and Diabetes Value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageStr.toIntOrNull()
            val diabetesValue = diabetesValueStr.toFloatOrNull()

            if (age == null || diabetesValue == null) {
                Toast.makeText(this, "Please enter valid Age and Diabetes Value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val profile = UserProfile(
                id = 1, // Single user for simplicity
                name = name,
                age = age,
                gender = gender,
                diabetesValue = diabetesValue,
                about = about,
                otherInfo = otherInfo
            )

            CoroutineScope(Dispatchers.IO).launch {
                val db = DatabaseModule.provideDatabase(this@ProfileActivity)
                val existingProfile = db.userProfileDao().getProfile()
                if (existingProfile == null) {
                    db.userProfileDao().insertProfile(profile)
                } else {
                    db.userProfileDao().updateProfile(profile)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Return to HomeActivity
                }
            }
        }
    }
}