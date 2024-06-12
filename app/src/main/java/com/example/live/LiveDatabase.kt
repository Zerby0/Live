package com.example.live

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [StepCount::class, Achievement::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LiveDatabase : RoomDatabase() {
    abstract fun stepCountDao(): StepCountDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: LiveDatabase? = null

        fun getDatabase(context: Context): LiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LiveDatabase::class.java,
                    "live_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}