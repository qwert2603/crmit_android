package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class CabinetPresenter : LRPresenter<Any, String, CabinetViewState, CabinetView>(DiHolder.uiSchedulerProvider) {

    private val loginResultSingle = Single
            .fromCallable { DiHolder.userSettingsRepo.loginResult!! }
            .cache()

    private fun getFioFromServer() = loginResultSingle
            .flatMap { loginResult ->
                when (loginResult.accountType) {
                    AccountType.MASTER -> DiHolder.rest.getMasterDetails(loginResult.detailsId)
                            .doOnSuccess { DiHolder.masterDaoInterface.saveItem(it) }
                            .map { it.fio }
                    AccountType.TEACHER -> DiHolder.rest.getTeacherDetails(loginResult.detailsId)
                            .doOnSuccess { DiHolder.teacherDaoInterface.saveItem(it) }
                            .map { it.fio }
                }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }
            .doOnSuccess { DiHolder.userSettingsRepo.displayFio = it }

    private fun getFioFromCache() = loginResultSingle
            .map { loginResult ->
                when (loginResult.accountType) {
                    AccountType.MASTER -> DiHolder.masterDaoInterface.getItem(loginResult.detailsId)?.fio
                    AccountType.TEACHER -> DiHolder.teacherDaoInterface.getItem(loginResult.detailsId)?.fio
                } ?: DiHolder.userSettingsRepo.displayFio
            }
            .doOnSuccess { viewActions.onNext(CabinetViewAction.ShowingCachedData) }
            .doOnSuccess { DiHolder.userSettingsRepo.displayFio = it }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override val initialState = CabinetViewState(EMPTY_LR_MODEL, null, null, null, false)

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges(),
            loadIntent
                    .switchMapSingle { loginResultSingle }
                    .map { CabinetPartialChange.AuthedUserLoaded(it) },
            intent { it.logoutClicks() }
                    .switchMap { _ ->
                        Completable
                                .merge(listOf(
                                        DiHolder.rest.logout().onErrorComplete(),
                                        Completable.fromAction {
                                            DiHolder.userSettingsRepo.loginResult = null
                                            DiHolder.userSettingsRepo.displayFio = null
                                            DiHolder.clearDB()
                                        }
                                ))
                                .doOnComplete { viewActions.onNext(CabinetViewAction.MoveToLogin) }
                                .toSingleDefault<PartialChange>(CabinetPartialChange.LogoutSuccess)
                                .toObservable()
                                .startWith(CabinetPartialChange.LogoutStarted)
                                .subscribeOn(DiHolder.modelSchedulersProvider.io)
                    }
    )

    override fun initialModelSingle(additionalKey: Any): Single<String> = getFioFromServer()
            .doOnError { LogUtils.e("CabinetPresenter getFioFromServer", it) }
            .onErrorResumeNext { getFioFromCache() }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<String> = getFioFromServer()

    override fun CabinetViewState.applyInitialModel(i: String) = copy(fio = i)

    override fun stateReducer(vs: CabinetViewState, change: PartialChange): CabinetViewState {
        if (change !is CabinetPartialChange) return super.stateReducer(vs, change)
        return when (change) {
            is CabinetPartialChange.AuthedUserLoaded -> vs.copy(
                    accountType = change.loginResult.accountType,
                    detailsId = change.loginResult.detailsId
            )
            CabinetPartialChange.LogoutStarted -> vs.copy(isLogout = true)
            CabinetPartialChange.LogoutSuccess -> vs.copy(isLogout = false)
        }
    }

    override fun bindIntents() {
        super.bindIntents()

        intent { it.onFioClicks() }
                .switchMapCompletable { _ ->
                    loginResultSingle
                            .doOnSuccess { viewActions.onNext(CabinetViewAction.MoveToUserDetails(it.accountType, it.detailsId)) }
                            .ignoreElement()
                            .onErrorComplete()
                }
                .toObservable<Any>()
                .subscribeToView()
    }
}