package com.example.newscan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_scans")
data class RecentScan(
    @PrimaryKey val barcode: String,
    val productName: String,
    val scanTime: Long // Timestamp for sorting
)