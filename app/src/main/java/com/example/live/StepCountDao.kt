package com.example.live

import android.content.ContentValues
import android.util.Log
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
    fun getStepCountForDate(date: String): LiveData<StepCount>?

    @Query("SELECT * FROM step_count")
    fun getAllStepCounts(): LiveData<List<StepCount>>

    @Query("SELECT SUM(steps) FROM step_count")
    fun getTotalSteps(): LiveData<Int?>

    @Query("SELECT date FROM step_count WHERE steps >= 10000 ORDER BY date ASC")
    fun getDaysWithMinimumSteps(): LiveData<List<String>>
}