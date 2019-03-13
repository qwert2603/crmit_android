package com.qwert2603.crmit_android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(searchField = "name", orderBy = "name")
data class Section(
        @PrimaryKey override val id: Long,
        val name: String,
        val price: Int,
        val groups: List<GroupBrief>
) : IdentifiableLong