package com.example.live

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllSync(): List<Achievement>

    @Query("SELECT * FROM achievements")
    fun getAll(): LiveData<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg achievements: Achievement)

    @Update
    suspend fun update(achievement: Achievement)
}