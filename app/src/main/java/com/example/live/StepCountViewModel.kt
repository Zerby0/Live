package com.example.live

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StepCountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StepCountRepository
    private val allStepCounts: LiveData<List<StepCount>>

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

    fun getWeeklySteps(): LiveData<List<StepCount>> {
        val weeklySteps = MutableLiveData<List<StepCount>>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<String>()

        for (i in 0 until 7) {
            val date = sdf.format(calendar.time)
            dates.add(date)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        viewModelScope.launch {
            val steps = repository.getStepCountsForDates(dates)
            weeklySteps.postValue(steps)
        }

        return weeklySteps
    }
}