package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class Teacher(
        override val id: Long,
        val fio: String,
        val lessonsCount: Int,
        val phone: String,
        val systemUser: SystemUser,
        val groups: List<Group>
) : IdentifiableLong {
    data class Group(
            val id: Long,
            val name: String
    )
}