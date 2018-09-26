package com.qwert2603.crmit_android.payments

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Payment
import com.qwert2603.crmit_android.entity.UploadStatus

@GenerateLRChanger
data class PaymentsViewState(
        override val lrModel: LRModel,
        val monthNumber: Int,
        val payments: List<Payment>?,
        val uploadingPaymentsStatuses: Map<Long, UploadStatus>,
        val authedUserAccountType: AccountType?,
        val authedUserDetailsId: Long?
) : LRViewState {
    fun isUserCanConfirm() = authedUserAccountType == AccountType.MASTER
}