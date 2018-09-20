package com.qwert2603.crmit_android.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.Filter
import com.qwert2603.dao_generator.FilterType
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(
        filters = [Filter("groupId", FilterType.LONG)],
        orderBy = "studentFio"
)
data class StudentInGroup(
        @PrimaryKey override val id: Long,
        val systemUserEnabled: Boolean,
        val studentId: Long,
        val studentFio: String,
        val groupId: Long,
        val discount: Int,
        val enterMonth: Int,
        val exitMonth: Int,
        val lessonsAttendedCount: Int
) : IdentifiableLong