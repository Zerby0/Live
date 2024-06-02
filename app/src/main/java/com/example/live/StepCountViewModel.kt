package com.example.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StepCountViewModel(private val stepCountDao: StepCountDao) : ViewModel() {

    suspend fun getStepCountForDateSync(date: String): LiveData<StepCount>? {
        return withContext(Dispatchers.IO) {
            stepCountDao.getStepCountForDate(date)
        }
    }

    fun insertStepCount(stepCount: StepCount) {
        viewModelScope.launch {
            stepCountDao.insert(stepCount)
        }
    }
}
