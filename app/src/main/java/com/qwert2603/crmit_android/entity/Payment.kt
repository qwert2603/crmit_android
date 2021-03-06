package com.qwert2603.crmit_android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.rest.params.SavePaymentParams
import com.qwert2603.dao_generator.Filter
import com.qwert2603.dao_generator.FilterType
import com.qwert2603.dao_generator.GenerateDao

@Entity
@GenerateDao(
        filters = [Filter("groupId", FilterType.LONG)],
        orderBy = "studentFio"
)
data class Payment(
        @PrimaryKey override val id: Long,
        val studentInGroupId: Long,
        val studentId: Long,
        val studentFio: String,
        val groupId: Long,
        val groupName: String,
        val monthNumber: Int,
        val value: Int,
        val cash: Boolean,
        val confirmed: Boolean,
        val comment: String,
        val needToPay: Int // price - discount == maxValue
) : IdentifiableLong {
    fun toSavePaymentParams() = SavePaymentParams(
            paymentId = id,
            value = value,
            comment = comment,
            cash = cash,
            confirmed = confirmed
    )
}