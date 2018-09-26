package com.qwert2603.crmit_android.payments_in_group

import com.qwert2603.andrlib.base.mvi.load_refresh.LRView
import io.reactivex.Observable

interface PaymentsInGroupView : LRView<PaymentsInGroupViewState> {
    fun monthSelected(): Observable<Int>
}