package com.qwert2603.crmit_android.payments_in_group

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.GroupFull

@GenerateLRChanger
data class PaymentsInGroupViewState(
        override val lrModel: LRModel,
        val groupFull: GroupFull?,
        val authedUserAccountType: AccountType?,
        val authedUserDetailsId: Long?,
        val selectedMonth: Int
) : LRViewState {
    fun canUserSeePayments() = when (authedUserAccountType) {
        AccountType.MASTER -> true
        AccountType.TEACHER -> authedUserDetailsId != null && authedUserDetailsId == groupFull?.teacherId
        null -> false
    }
}