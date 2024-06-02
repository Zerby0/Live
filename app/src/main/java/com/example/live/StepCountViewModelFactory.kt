package com.example.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StepCountViewModelFactory(private val stepCountDao: StepCountDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepCountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StepCountViewModel(stepCountDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}