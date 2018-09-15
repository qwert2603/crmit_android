package com.qwert2603.crmit_android.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "name")
data class GroupFull(
        @PrimaryKey val id: Long,
        val name: String,
        val teacherFio: String,
        val sectionName: String,
        val startMonth: Int,
        val endMonth: Int,
        val studentsCount: Int,
        val lessonsDoneCount: Int
)