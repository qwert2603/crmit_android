package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong
import java.text.SimpleDateFormat
import java.util.*

data class StudentFull(
        override val id: Long,
        val systemUser: SystemUser,
        val filled: Boolean,
        val fio: String,
        val birthDate: String,
        val birthPlace: String,
        val registrationPlace: String,
        val actualAddress: String,
        val additionalInfo: String?,
        val knownFrom: String?,
        val school: School,
        val grade: String,
        val shift: String,
        val phone: String?,
        val contactPhoneNumber: String,
        val contactPhoneWho: String,
        val citizenshipName: String,
        val mother: Parent?,
        val father: Parent?,
        val groups: List<Group>
) : IdentifiableLong {
    data class Group(
            val id: Long,
            val name: String,
            val teacherFio: String
    )

    fun showingBirthDate(): String = BIRTH_DATE_FORMAT_SHOWING.format(BIRTH_DATE_FORMAT.parse(birthDate))

    companion object {
        private val BIRTH_DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        private val BIRTH_DATE_FORMAT_SHOWING = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    }
}