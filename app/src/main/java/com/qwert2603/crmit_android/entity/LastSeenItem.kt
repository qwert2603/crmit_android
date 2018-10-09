package com.qwert2603.crmit_android.entity

import com.qwert2603.andrlib.model.IdentifiableLong

data class LastSeenItem(
        override val id: Long,
        val login: String,
        val lastSeen: String,
        val lastSeenWhere: Int,
        val systemRoleName: String,
        val fio: String
) : IdentifiableLong