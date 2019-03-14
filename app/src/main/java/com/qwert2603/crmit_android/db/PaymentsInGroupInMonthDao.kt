package com.qwert2603.crmit_android.db

import androidx.room.Dao
import androidx.room.Query
import com.qwert2603.crmit_android.entity.Payment

@Dao
interface PaymentsInGroupInMonthDao {

    @Query("SELECT * FROM Payment WHERE groupId = :groupId AND monthNumber = :monthNumber ORDER BY studentFio")
    fun getPaymentsInGroupInMonth(groupId: Long, monthNumber: Int): List<Payment>
}