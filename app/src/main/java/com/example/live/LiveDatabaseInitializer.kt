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
        ).build()
    }

    fun getInstance(): LiveDatabase {
        return instance ?: throw IllegalStateException("Database not initialized")
    }
}