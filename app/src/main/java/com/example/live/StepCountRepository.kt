package com.example.live

import android.util.Log
import androidx.lifecycle.LiveData

class StepCountRepository(private val stepCountDao: StepCountDao) {
    fun getStepCountForDate(date: String): LiveData<StepCount>? {
        return stepCountDao.getStepCountForDate(date)
    }

    fun getAllStepCounts(): LiveData<List<StepCount>> {
        return stepCountDao.getAllStepCounts()
    }

    fun getTotalSteps(): LiveData<Int?> {
        return stepCountDao.getTotalSteps()
    }

    fun getDaysWithMinimumSteps(): LiveData<List<String>> {
        return stepCountDao.getDaysWithMinimumSteps()
    }

    suspend fun insert(stepCount: StepCount) {
        stepCountDao.insert(stepCount)
    }

    suspend fun getStepCountsForDates(dates: List<String>): List<StepCount> {
        return stepCountDao.getStepCountsForDates(dates)
    }
}