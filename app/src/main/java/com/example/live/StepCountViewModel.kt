package com.example.live

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StepCountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StepCountRepository
    val allStepCounts: LiveData<List<StepCount>>

    init {
        val stepCountDao = LiveDatabase.getDatabase(application).stepCountDao()
        repository = StepCountRepository(stepCountDao)
        allStepCounts = repository.getAllStepCounts()
    }

    fun getStepCountForDate(date: String): LiveData<StepCount>? {
        return repository.getStepCountForDate(date)
    }

    fun getTotalSteps(): LiveData<Int?> {
        return repository.getTotalSteps()
    }

    fun getDaysWithMinimumSteps(): LiveData<List<String>> {
        return repository.getDaysWithMinimumSteps()
    }

    fun insert(stepCount: StepCount) = viewModelScope.launch {
        repository.insert(stepCount)
    }
}