package com.example.live

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val title: String,
    val description: String,
    val condition: List<Int>,
    var isCompleted: Boolean
)