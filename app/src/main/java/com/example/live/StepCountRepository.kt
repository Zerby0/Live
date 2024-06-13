package com.example.live

import androidx.lifecycle.LiveData

class StepCountRepository(private val stepCountDao: StepCountDao) {
    fun getStepCountForDate(date: String): LiveData<StepCount?> {
        return stepCountDao.getStepCountForDate(date)
    }

    fun getTotalSteps(): LiveData<Int?> {
        return stepCountDao.getTotalSteps()
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
    suspend fun getWeeklySteps(startDate: String, endDate: String): List<StepCount> {
        return stepCountDao.getStepsForDateRange(startDate, endDate)
    }

    suspend fun getDayWithLeastSteps(): StepCount? {
        return stepCountDao.getDayWithLeastSteps()
    }
}