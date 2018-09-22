package com.qwert2603.crmit_android.login

import com.qwert2603.andrlib.base.mvi.BaseView
import io.reactivex.Observable

interface LoginView : BaseView<LoginViewState> {
    fun loginChanges(): Observable<String>
    fun passwordChanges(): Observable<String>
    fun loginClicks(): Observable<Any>
}