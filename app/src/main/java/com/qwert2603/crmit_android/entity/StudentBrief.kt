package com.qwert2603.crmit_android.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "fio", orderBy = "filled, fio")
data class StudentBrief(
        @PrimaryKey override val id: Long,
        val fio: String,
        val contactPhoneNumber: String,
        val contactPhoneWho: String,
        val filled: Boolean,
        @Embedded(prefix = "systemUser_") val systemUser: SystemUser,
        val schoolName: String,
        val grade: String,
        val shift: String,
        val groups: List<GroupBrief>,
        val lessonsAttendedCount: Int
) : IdentifiableLong