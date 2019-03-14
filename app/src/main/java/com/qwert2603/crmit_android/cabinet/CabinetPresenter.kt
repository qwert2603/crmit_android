package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.crmit_android.BuildConfig
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.BotAccountIsNotSupportedException
import com.qwert2603.crmit_android.entity.Lesson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class CabinetPresenter : LRPresenter<Any, CabinetInitialModel, CabinetViewState, CabinetView>(DiHolder.uiSchedulerProvider) {

    companion object {
        private const val LAST_LESSONS_COUNT = 20
    }

    private val loginResultSingle = DiHolder.userSettingsRepo
            .getLoginResultOrMoveToLogin()
            .cache()

    private fun getFioFromCache(): Single<String> = loginResultSingle
            .map { loginResult ->
                when (loginResult.accountType) {
                    AccountType.MASTER -> DiHolder.masterDaoInterface.getItem(loginResult.detailsId)?.fio
                    AccountType.TEACHER -> DiHolder.teacherDaoInterface.getItem(loginResult.detailsId)?.fio
                    AccountType.DEVELOPER -> DiHolder.developerDaoInterface.getItem(loginResult.detailsId)?.fio
                    AccountType.BOT -> throw BotAccountIsNotSupportedException()
                } ?: DiHolder.userSettingsRepo.displayFio!!
            }
            .doOnSuccess { DiHolder.userSettingsRepo.displayFio = it }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    private fun getLastLessonsFromCache(): Single<List<Lesson>> = loginResultSingle
            .map { loginResult ->
                when (loginResult.accountType) {
                    AccountType.MASTER -> DiHolder.lastLessonDao.getLastLessons(LAST_LESSONS_COUNT)
                    AccountType.TEACHER -> DiHolder.lastLessonDao.getLastLessonsForTeacher(loginResult.detailsId, LAST_LESSONS_COUNT)
                    AccountType.DEVELOPER -> DiHolder.lastLessonDao.getLastLessons(LAST_LESSONS_COUNT)
                    AccountType.BOT -> throw BotAccountIsNotSupportedException()
                }
            }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override val initialState = CabinetViewState(EMPTY_LR_MODEL, null, null, null, false)

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges(),
            loadIntent
                    .switchMapSingle { loginResultSingle }
                    .map { CabinetPartialChange.AuthedUserLoaded(it) },
            intent { it.logoutClicks() }
                    .switchMap {
                        Completable
                                .merge(listOf(
                                        DiHolder.rest.logout().onErrorComplete(),
                                        Completable.fromAction {
                                            DiHolder.userSettingsRepo.clearUserInfo()
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

    override fun initialModelSingle(additionalKey: Any): Single<CabinetInitialModel> = initialModelSingleRefresh(additionalKey)
            .onErrorResumeNext {
                viewActions.onNext(CabinetViewAction.ShowingCachedData)
                Single
                        .zip(
                                getFioFromCache(),
                                getLastLessonsFromCache(),
                                BiFunction { fio: String, lastLessons: List<Lesson> -> CabinetInitialModel(fio, lastLessons) }
                        )
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<CabinetInitialModel> = DiHolder.rest
            .getCabinetInfo(LAST_LESSONS_COUNT)
            .doOnSuccess {
                DiHolder.lessonDao.addItems(it.lastLessons)
                DiHolder.userSettingsRepo.displayFio = it.fio
            }
            .doOnSuccess {
                if (it.actualAppBuildCode > BuildConfig.VERSION_CODE) {
                    viewActions.onNext(CabinetViewAction.ShowUpdateAvailable)
                }
            }
            .map { CabinetInitialModel(fio = it.fio, lastLessons = it.lastLessons) }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun CabinetViewState.applyInitialModel(i: CabinetInitialModel) = copy(
            fio = i.fio,
            lastLessons = i.lastLessons
    )

    override fun stateReducer(vs: CabinetViewState, change: PartialChange): CabinetViewState {
        if (change !is CabinetPartialChange) return super.stateReducer(vs, change)
        return when (change) {
            is CabinetPartialChange.AuthedUserLoaded -> vs.copy(loginResult = change.loginResult)
            CabinetPartialChange.LogoutStarted -> vs.copy(isLogout = true)
            CabinetPartialChange.LogoutSuccess -> vs.copy(isLogout = false)
        }
    }

    override fun bindIntents() {
        super.bindIntents()

        intent { it.onFioClicks() }
                .switchMapCompletable {
                    loginResultSingle
                            .doOnSuccess { viewActions.onNext(CabinetViewAction.MoveToUserDetails(it.accountType, it.detailsId)) }
                            .ignoreElement()
                            .onErrorComplete()
                }
                .toObservable<Any>()
                .subscribeToView()
    }
}