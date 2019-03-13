package com.qwert2603.crmit_android.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qwert2603.crmit_android.entity.GroupBrief

class GroupBriefListConverter {

    private val gson = Gson()

    private val type = object : TypeToken<List<GroupBrief>>() {}.type

    @TypeConverter
    fun toJsonString(groupsBrief: List<GroupBrief>): String = gson.toJson(groupsBrief)

    @TypeConverter
    fun fromJsonString(json: String): List<GroupBrief> = gson.fromJson(json, type)

}