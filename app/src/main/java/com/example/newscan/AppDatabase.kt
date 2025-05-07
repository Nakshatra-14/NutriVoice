package com.example.newscan

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class, RecentScan::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun recentScanDao(): RecentScanDao
}