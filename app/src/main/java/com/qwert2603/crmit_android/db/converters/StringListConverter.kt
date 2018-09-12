package com.qwert2603.crmit_android.db.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {

    private val gson = Gson()

    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun toJsonString(strings: List<String>): String = gson.toJson(strings)

    @TypeConverter
    fun fromJsonString(json: String): List<String> = gson.fromJson(json, type)

}