package com.example.newscan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfile(): UserProfile?

    @Insert
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)
}