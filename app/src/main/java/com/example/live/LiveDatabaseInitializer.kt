package com.example.live

import android.content.Context
import androidx.room.Room

object LiveDatabaseInitializer {
    private var instance: LiveDatabase? = null

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context.applicationContext,
            LiveDatabase::class.java,
            "my_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Volatile
    private var INSTANCE: LiveDatabase? = null

    fun getInstance(context: Context): LiveDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                LiveDatabase::class.java,
                "live_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}