package com.qwert2603.crmit_android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.dao_generator.Filter
import com.qwert2603.dao_generator.FilterType
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(
        filters = [Filter("lessonId", FilterType.LONG)],
        orderBy = "studentFio"
)
data class Attending(
        @PrimaryKey override val id: Long,
        val lessonId: Long,
        val studentId: Long,
        val studentFio: String,
        val state: Int
) : IdentifiableLong {
    companion object {
        const val ATTENDING_STATE_WAS_NOT = 0
        const val ATTENDING_STATE_WAS = 1
        const val ATTENDING_STATE_WAS_NOT_ILL = 2

        val ATTENDING_STATES = listOf(
                ATTENDING_STATE_WAS_NOT,
                ATTENDING_STATE_WAS,
                ATTENDING_STATE_WAS_NOT_ILL
        )
    }
}