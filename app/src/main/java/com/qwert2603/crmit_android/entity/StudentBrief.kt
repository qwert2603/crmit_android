package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class StudentBrief(
        override val id: Long,
        val fio: String,
        val contactPhoneNumber: String,
        val contactPhoneWho: String,
        val filled: Boolean,
        val systemUser: SystemUser,
        val schoolName: String,
        val grade: String,
        val shift: String,
        val groups: List<Group>
) : IdentifiableLong {
    data class Group(
            val id: Long,
            val name: String
    )
}