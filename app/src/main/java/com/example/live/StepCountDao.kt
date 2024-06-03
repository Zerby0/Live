package com.example.live

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepCount: StepCount)

    @Query("SELECT * FROM step_count WHERE date = :date")
    suspend fun getStepCountForDateSync(date: String): StepCount?

    @Query("SELECT * FROM step_count WHERE date = :date")
    fun getStepCountForDate(date: String): LiveData<StepCount>?

    @Query("SELECT * FROM step_count")
    suspend fun getAllStepCounts(): List<StepCount>

    // Tutti i passi
    @Query("SELECT SUM(steps) FROM step_count")
    suspend fun getTotalSteps(): Int?

    // Giorni di fila con più di 10000 passi
    @Query("SELECT date FROM step_count WHERE steps >= 10000 ORDER BY date ASC")
    suspend fun getDaysWithMinimumSteps(): List<String>
}