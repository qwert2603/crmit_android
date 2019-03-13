package com.qwert2603.crmit_android.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qwert2603.crmit_android.entity.ScheduleItem

class ScheduleItemListConverter {

    private val gson = Gson()

    private val type = object : TypeToken<List<ScheduleItem>>() {}.type

    @TypeConverter
    fun toJsonString(groupsBrief: List<ScheduleItem>): String = gson.toJson(groupsBrief)

    @TypeConverter
    fun fromJsonString(json: String): List<ScheduleItem> = gson.fromJson(json, type)

}