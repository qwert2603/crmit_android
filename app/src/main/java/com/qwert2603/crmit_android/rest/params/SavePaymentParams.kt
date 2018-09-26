package com.qwert2603.crmit_android.rest.params

data class SavePaymentParams(
        val paymentId: Long,
        val value: Int,
        val comment: String,
        val cash: Boolean,
        val confirmed: Boolean
)