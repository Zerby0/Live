package com.example.live

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.round

@Entity(tableName = "step_count")
data class StepCount(
    @PrimaryKey val date: String,
    var steps: Int,
    val calories: Double = calculateCalories(steps)
) {
    companion object {
        private fun calculateCalories(steps: Int): Double {
            return round(steps * 0.04)
        }
    }
}