package com.qwert2603.crmit_android.payments

import com.qwert2603.andrlib.base.mvi.ViewAction

sealed class PaymentsViewAction : ViewAction {
    data class AskToEditValue(val paymentId: Long, val value: Int, val maxValue: Int) : PaymentsViewAction()
    data class AskToEditComment(val paymentId: Long, val comment: String) : PaymentsViewAction()
}