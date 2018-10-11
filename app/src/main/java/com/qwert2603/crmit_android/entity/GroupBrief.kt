package com.qwert2603.crmit_android.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong

@Entity
data class GroupBrief(
        @PrimaryKey override val id: Long,
        val name: String,
        val teacherId: Long,
        val teacherFio: String,
        val startMonth: Int,
        val endMonth: Int
) : IdentifiableLong