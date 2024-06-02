package com.example.live

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_count")
data class StepCount(
    @PrimaryKey val date: String,
    val count: Int
)