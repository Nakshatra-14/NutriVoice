package com.example.newscan.data

import android.content.Context
import androidx.room.Room
import com.example.newscan.AppDatabase

object DatabaseModule {
    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "nutrivoice_db"
            ).build().also { database = it }
        }
    }
}