package com.qwert2603.crmit_android.login

import com.google.gson.Gson
import com.qwert2603.andrlib.base.mvi.BasePresenter
import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.LoginErrorReason
import com.qwert2603.crmit_android.entity.LoginResultError
import com.qwert2603.crmit_android.rest.params.LoginParams
import com.qwert2603.crmit_android.util.makePair
import com.qwert2603.crmit_android.util.secondOfTwo
import io.reactivex.Observable
import retrofit2.HttpException

class LoginPresenter : BasePresenter<LoginView, LoginViewState>(DiHolder.uiSchedulerProvider) {
    override val initialState = LoginViewState("", "", false)

    private val loginChangesIntent = intent { it.loginChanges() }.shareAfterViewSubscribed()
    private val passwordChangesIntent = intent { it.passwordChanges() }.shareAfterViewSubscribed()

    private val credentials = Observable.combineLatest(loginChangesIntent, passwordChangesIntent, makePair())

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loginChangesIntent.map { LoginPartialChange.LoginChanged(it) },
            passwordChangesIntent.map { LoginPartialChange.PasswordChanged(it) },
            intent { it.loginClicks() }
                    .withLatestFrom(credentials, secondOfTwo())
                    .switchMap { (login, password) ->
                        DiHolder.rest
                                .login(LoginParams(login, password))
                                .doOnSuccess { DiHolder.userSettingsRepo.loginResult = it }
                                .subscribeOn(DiHolder.modelSchedulersProvider.io)
                                .map<LoginPartialChange> { LoginPartialChange.LoggingSuccess }
                                .doOnError {
                                    val loginErrorReason = if (it is HttpException && it.code() == DiHolder.RESPONSE_CODE_BAD_REQUEST) {
                                        try {
                                            val string = it.response().errorBody()!!.string()
                                            Gson().fromJson(string, LoginResultError::class.java).loginErrorReason
                                        } catch (t: Throwable) {
                                            LogUtils.e("login error", t)
                                            LoginErrorReason.ANOTHER
                                        }
                                    } else {
                                        LoginErrorReason.ANOTHER
                                    }
                                    viewActions.onNext(LoginViewAction.ShowLoginError(loginErrorReason))
                                }
                                .doOnSuccess { viewActions.onNext(LoginViewAction.MoveToMastersList) }
                                .onErrorReturn { LoginPartialChange.LoggingError }
                                .toObservable()
                                .startWith(LoginPartialChange.LoggingStarted)
                    }
    )

    override fun stateReducer(vs: LoginViewState, change: PartialChange): LoginViewState {
        if (change !is LoginPartialChange) throw Exception()
        return when (change) {
            is LoginPartialChange.LoginChanged -> vs.copy(login = change.login)
            is LoginPartialChange.PasswordChanged -> vs.copy(password = change.password)
            LoginPartialChange.LoggingStarted -> vs.copy(isLogging = true)
            LoginPartialChange.LoggingError -> vs.copy(isLogging = false)
            LoginPartialChange.LoggingSuccess -> vs.copy(isLogging = false)
        }
    }
}