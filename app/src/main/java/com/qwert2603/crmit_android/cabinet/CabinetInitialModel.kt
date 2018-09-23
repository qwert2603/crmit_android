package com.qwert2603.crmit_android.cabinet

import com.qwert2603.crmit_android.entity.AccountType

data class CabinetInitialModel(
        val fio: String,
        val accountType: AccountType,
        val detailsId: Long
)