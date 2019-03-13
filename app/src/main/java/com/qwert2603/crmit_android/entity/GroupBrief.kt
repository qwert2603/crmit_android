package com.qwert2603.crmit_android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong

@Entity
data class GroupBrief(
        @PrimaryKey override val id: Long,
        val name: String,
        val teacherId: Long,
        val teacherFio: String,
        val startMonth: Int,
        val endMonth: Int,
        val sumNotConfirmed: Int
) : IdentifiableLong