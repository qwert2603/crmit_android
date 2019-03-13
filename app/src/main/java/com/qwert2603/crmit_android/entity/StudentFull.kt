package com.qwert2603.crmit_android.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "fio")
data class StudentFull(
        @PrimaryKey override val id: Long,
        @Embedded(prefix = "systemUser_") val systemUser: SystemUser,
        val filled: Boolean,
        val fio: String,
        val birthDate: String,
        val birthPlace: String,
        val registrationPlace: String,
        val actualAddress: String,
        val additionalInfo: String?,
        val knownFrom: String?,
        @Embedded(prefix = "school_") val school: School,
        val grade: String,
        val shift: String,
        val phone: String?,
        val contactPhoneNumber: String,
        val contactPhoneWho: String,
        val citizenshipName: String,
        @Embedded(prefix = "mother_") val mother: Parent?,
        @Embedded(prefix = "father_") val father: Parent?,
        val groups: List<GroupBrief>,
        val lessonsAttendedCount: Int
) : IdentifiableLong