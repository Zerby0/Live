package com.example.live

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromConditionList(condition: List<Int>): String {
        return Gson().toJson(condition)
    }

    @TypeConverter
    fun toConditionList(conditionString: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(conditionString, listType)
    }
}