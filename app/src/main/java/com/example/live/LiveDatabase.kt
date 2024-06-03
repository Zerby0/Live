package com.example.live

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepCount::class], version = 3)
abstract class LiveDatabase : RoomDatabase() {
    abstract fun stepCountDao(): StepCountDao

    companion object {
        @Volatile
        private var INSTANCE: LiveDatabase? = null

        fun getDatabase(context: Context): LiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LiveDatabase::class.java,
                    "live_database"
                ).fallbackToDestructiveMigration() // Permette migrazioni distruttive
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}