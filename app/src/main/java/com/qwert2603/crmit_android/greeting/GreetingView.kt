package com.qwert2603.crmit_android.greeting

import com.qwert2603.andrlib.base.mvi.BaseView
import io.reactivex.Observable

interface GreetingView : BaseView<GreetingViewState> {
    fun currentIndexChanges(): Observable<Int>
    fun backClicks(): Observable<Any>
    fun forwardClicks(): Observable<Any>
    fun startClicks(): Observable<Any>
}