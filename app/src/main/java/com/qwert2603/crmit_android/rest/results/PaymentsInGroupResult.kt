package com.qwert2603.crmit_android.rest.results

import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.entity.Payment

data class PaymentsInGroupResult(
        val group: GroupBrief,
        val payments: List<Payment>
)