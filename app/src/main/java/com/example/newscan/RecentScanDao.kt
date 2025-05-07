package com.example.newscan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecentScanDao {
    @Insert
    suspend fun insertScan(scan: RecentScan)

    @Query("SELECT * FROM recent_scans ORDER BY scanTime DESC LIMIT 10")
    suspend fun getRecentScans(): List<RecentScan>
}