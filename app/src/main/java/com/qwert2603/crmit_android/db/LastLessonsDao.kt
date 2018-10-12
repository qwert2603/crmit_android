package com.qwert2603.crmit_android.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.qwert2603.crmit_android.entity.Lesson

@Dao
interface LastLessonsDao {

    @Query("SELECT * FROM Lesson ORDER BY date DESC LIMIT :count")
    fun getLastLessons(count: Int): List<Lesson>

    @Query("SELECT * FROM Lesson WHERE teacherId = :teacherId ORDER BY date DESC LIMIT :count")
    fun getLastLessonsForTeacher(teacherId: Long, count: Int): List<Lesson>
}