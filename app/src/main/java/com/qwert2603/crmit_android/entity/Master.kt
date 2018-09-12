package com.qwert2603.crmit_android.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "fio")
data class Master(
        @PrimaryKey override val id: Long,
        val fio: String,
        @Embedded(prefix = "systemUser_") val systemUser: SystemUser
) : IdentifiableLong