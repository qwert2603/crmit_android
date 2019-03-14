package com.qwert2603.crmit_android.db

import androidx.room.Dao
import androidx.room.Query
import com.qwert2603.crmit_android.entity.Lesson
import com.qwert2603.crmit_android.rest.Rest
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface LastLessonsDao {

    companion object {
        private fun dateToday() = SimpleDateFormat(Rest.DATE_FORMAT, Locale.getDefault()).format(Date())
    }

    @Query("SELECT * FROM Lesson WHERE date <= :date ORDER BY date DESC, id LIMIT :count")
    fun getLastLessons(count: Int, date: String = dateToday()): List<Lesson>

    @Query("SELECT * FROM Lesson WHERE teacherId = :teacherId AND date <= :date ORDER BY date DESC, id LIMIT :count")
    fun getLastLessonsForTeacher(teacherId: Long, count: Int, date: String = dateToday()): List<Lesson>
}