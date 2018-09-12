package com.qwert2603.crmit_android.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong

@Entity
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
        val groups: List<GroupBrief>
) : IdentifiableLong