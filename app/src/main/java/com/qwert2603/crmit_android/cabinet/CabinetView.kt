package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.load_refresh.LRView
import io.reactivex.Observable

interface CabinetView : LRView<CabinetViewState> {
    fun onFioClicks(): Observable<Any>
    fun logoutClicks(): Observable<Any>
}