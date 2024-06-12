package com.example.live

import androidx.lifecycle.LiveData

class StepCountRepository(private val stepCountDao: StepCountDao) {
    fun getStepCountForDate(date: String): LiveData<StepCount?> {
        return stepCountDao.getStepCountForDate(date)
    }

    fun getAllStepCounts(): LiveData<List<StepCount>> {
        return stepCountDao.getAllStepCounts()
    }

    suspend fun getStepCountsForDates(dates: List<String>): List<StepCount> {
        return stepCountDao.getStepCountsForDates(dates)
    }

    fun getStepHistory(): LiveData<List<StepCount>>{
        return stepCountDao.getStepHistory()
    }
}