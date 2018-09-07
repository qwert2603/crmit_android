package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class Section(
        override val id: Long,
        val name: String,
        val price: Int,
        val groups: List<Group>
) : IdentifiableLong {
    data class Group(
            val id: Long,
            val name: String,
            val teacherFio: String
    )
}