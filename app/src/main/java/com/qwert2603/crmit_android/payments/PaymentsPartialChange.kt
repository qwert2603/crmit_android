package com.qwert2603.crmit_android.payments

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.entity.LoginResult
import com.qwert2603.crmit_android.entity.Payment

sealed class PaymentsPartialChange : PartialChange {
    data class AuthedUserLoaded(val loginResult: LoginResult) : PaymentsPartialChange()
    data class PaymentChanged(val payment: Payment) : PaymentsPartialChange()
    data class UploadPaymentStarted(val paymentId: Long) : PaymentsPartialChange()
    data class UploadPaymentError(val paymentId: Long) : PaymentsPartialChange()
    data class UploadPaymentSuccess(val paymentId: Long) : PaymentsPartialChange()
}