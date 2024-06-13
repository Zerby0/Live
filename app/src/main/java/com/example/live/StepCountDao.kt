package com.example.live

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepCountDao {
    @Query("SELECT * FROM step_count")
    fun getAllSync(): List<StepCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg stepCount: StepCount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepCount: StepCount)

    @Query("SELECT * FROM step_count")
    fun getAllStepCounts(): LiveData<List<StepCount>>

    @Query("SELECT * FROM step_count ORDER BY steps ASC LIMIT 1")
    suspend fun getDayWithLeastSteps(): StepCount?

    @Query("SELECT * FROM step_count WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getStepsForDateRange(startDate: String, endDate: String): List<StepCount>

    @Query("SELECT SUM(steps) FROM step_count")
    fun getTotalSteps(): LiveData<Int?>

    @Query("SELECT date FROM step_count WHERE steps >= 10000 ORDER BY date ASC")
    fun getDaysWithMinimumSteps(): LiveData<List<String>>

    @Query("SELECT * FROM step_count WHERE date = :date LIMIT 1")
    fun getStepCountForDate(date: String): LiveData<StepCount?>

    @Query("SELECT * FROM step_count ORDER BY date ASC")
    fun getStepHistory(): LiveData<List<StepCount>>

    @Query("SELECT * FROM step_count WHERE date IN (:dates)")
    suspend fun getStepCountsForDates(dates: List<String>): List<StepCount>
}