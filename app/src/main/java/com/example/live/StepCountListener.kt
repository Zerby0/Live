package com.example.live

interface StepCountListener {
    fun onStepCountChanged(stepCount: Int, calorieCount: Double)
}