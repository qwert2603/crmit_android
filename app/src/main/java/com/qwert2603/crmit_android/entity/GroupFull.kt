package com.qwert2603.crmit_android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "name")
data class GroupFull(
        @PrimaryKey val id: Long,
        val name: String,
        val teacherId: Long,
        val teacherFio: String,
        val sectionId: Long,
        val sectionName: String,
        val startMonth: Int,
        val endMonth: Int,
        val studentsCount: Int,
        val lessonsDoneCount: Int,
        val sumNotConfirmed: Int,
        val schedule: List<ScheduleItem>
) {
    fun toGroupBrief() = GroupBrief(
            id = id,
            name = name,
            teacherId = teacherId,
            teacherFio = teacherFio,
            startMonth = startMonth,
            endMonth = endMonth,
            sumNotConfirmed = sumNotConfirmed
    )

    fun monthsCount() = endMonth - startMonth + 1
}