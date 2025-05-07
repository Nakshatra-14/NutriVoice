package com.example.newscan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val gender: String,
    val diabetesValue: Float?,
    val about: String,
    val otherInfo: String
)