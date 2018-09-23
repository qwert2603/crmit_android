package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class CabinetPresenter : LRPresenter<Any, CabinetInitialModel, CabinetViewState, CabinetView>(DiHolder.uiSchedulerProvider) {

    private val loginResultSingle = Single
            .fromCallable { DiHolder.userSettingsRepo.loginResult!! }
            .cache()

    override val initialState = CabinetViewState(EMPTY_LR_MODEL, null, null, null, false)

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges(),
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

    override fun initialModelSingle(additionalKey: Any): Single<CabinetInitialModel> = loginResultSingle
            .flatMap { loginResult ->
                when (loginResult.accountType) {
                    AccountType.MASTER -> DiHolder.rest.getMasterDetails(loginResult.detailsId).map { it.fio }
                    AccountType.TEACHER -> DiHolder.rest.getTeacherDetails(loginResult.detailsId).map { it.fio }
                }
                        .onErrorReturnNotNull {
                            when (loginResult.accountType) {
                                AccountType.MASTER -> DiHolder.masterDaoInterface.getItem(loginResult.detailsId)?.fio
                                AccountType.TEACHER -> DiHolder.teacherDaoInterface.getItem(loginResult.detailsId)?.fio
                            }
                                    .let { it ?: DiHolder.userSettingsRepo.displayFio }
                                    ?.also { viewActions.onNext(CabinetViewAction.ShowingCachedData) }
                        }
                        .doOnSuccess { DiHolder.userSettingsRepo.displayFio = it }
                        .map { CabinetInitialModel(it, loginResult.accountType, loginResult.detailsId) }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<CabinetInitialModel> = loginResultSingle
            .flatMap { loginResult ->
                when (loginResult.accountType) {
                    AccountType.MASTER -> DiHolder.rest.getMasterDetails(loginResult.detailsId)
                            .map { it.fio }
                    AccountType.TEACHER -> DiHolder.rest.getTeacherDetails(loginResult.detailsId)
                            .map { it.fio }
                }
                        .doOnSuccess { DiHolder.userSettingsRepo.displayFio = it }
                        .map { CabinetInitialModel(it, loginResult.accountType, loginResult.detailsId) }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }

    override fun CabinetViewState.applyInitialModel(i: CabinetInitialModel) = copy(accountType = i.accountType, fio = i.fio, detailsId = i.detailsId)

    override fun stateReducer(vs: CabinetViewState, change: PartialChange): CabinetViewState {
        if (change !is CabinetPartialChange) return super.stateReducer(vs, change)
        return when (change) {
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

    companion object {
        private fun <T> Single<T>.onErrorReturnNotNull(valueProvider: () -> T?) = this
                .onErrorResumeNext { throwable ->
                    valueProvider()
                            ?.let { Single.just(it) }
                            ?: Single.error(throwable)
                }
    }
}