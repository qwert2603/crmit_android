package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.AccountType

@GenerateLRChanger
data class CabinetViewState(
        override val lrModel: LRModel,
        val fio: String?,
        val accountType: AccountType?,
        val detailsId: Long?,
        val isLogout: Boolean
) : LRViewState