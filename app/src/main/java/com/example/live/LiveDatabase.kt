package com.example.live

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepCount::class], version = 3)
abstract class LiveDatabase : RoomDatabase() {
    abstract fun stepCountDao(): StepCountDao
}