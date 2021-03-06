package com.qwert2603.crmit_android.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "fio", orderBy = "fio")
data class Teacher(
        @PrimaryKey override val id: Long,
        val fio: String,
        val lessonsDoneCount: Int,
        val phone: String,
        @Embedded(prefix = "systemUser_") val systemUser: SystemUser,
        val groups: List<GroupBrief>
) : IdentifiableLong