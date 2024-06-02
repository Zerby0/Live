package com.example.live

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepCount: StepCount)

    // Recupera i passi giornalieri per una data specifica
    @Query("SELECT * FROM step_count WHERE date = :date")
    suspend fun getStepCountForDate(date: String): StepCount?
}