package com.qwert2603.crmit_android.payments

import com.qwert2603.andrlib.base.mvi.load_refresh.LRView
import io.reactivex.Observable

interface PaymentsView : LRView<PaymentsViewState> {
    fun isCashChanges(): Observable<Pair<Long, Boolean>>
    fun isConfirmedChanges(): Observable<Pair<Long, Boolean>>

    fun askToEditValue(): Observable<Long>
    fun askToEditComment(): Observable<Long>

    fun valueChanges(): Observable<Pair<Long, Int>>
    fun commentChanges(): Observable<Pair<Long, String>>

    fun retryClicks(): Observable<Long>
}