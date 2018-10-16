package com.qwert2603.crmit_android.lessons_in_group

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Lesson
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class LessonsInGroupPresenter(private val groupId: Long, date: String)
    : LRPresenter<Any, List<Lesson>, LessonsInGroupViewState, LessonsInGroupView>(DiHolder.uiSchedulerProvider) {
    override val initialState = LessonsInGroupViewState(EMPTY_LR_MODEL, null, date)

    private val loadRefreshPartialChanges = loadRefreshPartialChanges().shareAfterViewSubscribed()

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            loadRefreshPartialChanges,
            intent { it.dateSelected() }
                    .map { LessonsInGroupPartialChange.SelectedDateChanged(it) }
    )

    private val daoInterface = DiHolder.lessonDao.wrap(groupId)

    private fun getLessonsListFromServer() = DiHolder.rest
            .getLessonsInGroup(groupId)
            .doOnSuccess {
                daoInterface.deleteAllItems()
                daoInterface.addItems(it)
            }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun initialModelSingle(additionalKey: Any): Single<List<Lesson>> = getLessonsListFromServer()
            .onErrorResumeNext { t ->
                LogUtils.e("LessonsInGroupPresenter getLessonsListFromServer", t)
                Single
                        .fromCallable { daoInterface.getItems() }
                        .flatMap {
                            if (it.isNotEmpty()) {
                                viewActions.onNext(LessonsInGroupViewAction.ShowingCachedData)
                                Single.just(it)
                            } else {
                                Single.error(Exception("no cache!"))
                            }
                        }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<List<Lesson>> = getLessonsListFromServer()

    override fun LessonsInGroupViewState.applyInitialModel(i: List<Lesson>) = copy(lessons = i)

    override fun stateReducer(vs: LessonsInGroupViewState, change: PartialChange): LessonsInGroupViewState {
        if (change !is LessonsInGroupPartialChange) return super.stateReducer(vs, change)
        return when (change) {
            is LessonsInGroupPartialChange.SelectedDateChanged -> vs.copy(selectedDate = change.date)
        }
    }

    override fun bindIntents() {
        super.bindIntents()

        loadRefreshPartialChanges
                .filter { it is LRPartialChange.InitialModelLoaded<*> }
                .firstOrError()
                .delay(2, TimeUnit.SECONDS)
                .doOnSuccess {
                    if (!DiHolder.userSettingsRepo.thereWillBeAttendingChangesCachingShown) {
                        DiHolder.userSettingsRepo.thereWillBeAttendingChangesCachingShown = true
                        viewActions.onNext(LessonsInGroupViewAction.ShowThereWillBeAttendingChangesCaching)
                    }
                }
                .toObservable()
                .subscribeToView()
    }
}