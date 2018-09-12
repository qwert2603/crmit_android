package com.qwert2603.crmit_android.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong

@Entity
data class Section(
        @PrimaryKey override val id: Long,
        val name: String,
        val price: Int,
        val groups: List<GroupBrief>
) : IdentifiableLong