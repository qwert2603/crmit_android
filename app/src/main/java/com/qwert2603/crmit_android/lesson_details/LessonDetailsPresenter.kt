package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Attending
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class LessonDetailsPresenter(private val lessonId: Long)
    : LRPresenter<Any, LessonDetailsInitialModel, LessonDetailsViewState, LessonDetailsView>(DiHolder.uiSchedulerProvider) {

    override val initialState = LessonDetailsViewState(EMPTY_LR_MODEL, null, null, null, emptyMap(), null, null)

    private val attendingStatesChangesIntent = intent { it.attendingStatesChanges() }.shareAfterViewSubscribed()

    private val loadRefreshPartialChanges = loadRefreshPartialChanges().shareAfterViewSubscribed()

    override val partialChanges: Observable<PartialChange> = Observable.merge(listOf(
            loadRefreshPartialChanges,
            loadIntent
                    .map { DiHolder.userSettingsRepo.loginResult }
                    .map { LessonDetailsPartialChange.AuthedUserLoaded(it) },
            attendingStatesChangesIntent
                    .map { LessonDetailsPartialChange.AttendingStateChanged(it.attendingId, it.attendingState) },
            attendingStatesChangesIntent
                    .concatMap { params ->
                        DiHolder.rest.saveAttendingState(params)
                                .doOnComplete {
                                    DiHolder.attendingDao
                                            .getItem(params.attendingId)
                                            ?.also { DiHolder.attendingDao.saveItem(it.copy(state = params.attendingState)) }
                                }
                                .toSingleDefault<LessonDetailsPartialChange>(LessonDetailsPartialChange.UploadAttendingStateSuccess(params.attendingId))
                                .onErrorReturnItem(LessonDetailsPartialChange.UploadAttendingStateError(params.attendingId))
                                .toObservable()
                                .startWith(LessonDetailsPartialChange.UploadAttendingStateStarted(params.attendingId))
                                .subscribeOn(DiHolder.modelSchedulersProvider.io)
                    }
    ))

    private fun getAttendingsFromServer() = DiHolder.rest
            .getAttendingsOfLesson(lessonId)
            .doOnSuccess { DiHolder.attendingDao.addItems(it) }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    private fun Single<List<Attending>>.toInitialModel(): Single<LessonDetailsInitialModel> = map { attendings ->
        val lesson = DiHolder.lessonDao
                .getItem(lessonId)
        LessonDetailsInitialModel(
                groupBrief = lesson
                        ?.groupId
                        ?.let { DiHolder.groupBriefDaoInterface.getItem(it) },
                date = lesson?.date,
                attendings = attendings
        )
    }

    override fun initialModelSingle(additionalKey: Any): Single<LessonDetailsInitialModel> = getAttendingsFromServer()
            .onErrorResumeNext { t ->
                LogUtils.e("LessonDetailsPresenter DiHolder.rest.getAttendingsOfLesson(lessonId)", t)
                Single
                        .fromCallable { DiHolder.attendingDao.getItems(lessonId, 0, Int.MAX_VALUE) }
                        .flatMap {
                            if (it.isNotEmpty()) {
                                viewActions.onNext(LessonDetailsViewAction.ShowingCachedData)
                                Single.just(it)
                            } else {
                                Single.error(Exception("no cache!"))
                            }
                        }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }
            .toInitialModel()

    override fun initialModelSingleRefresh(additionalKey: Any): Single<LessonDetailsInitialModel> = getAttendingsFromServer()
            .toInitialModel()

    override fun LessonDetailsViewState.applyInitialModel(i: LessonDetailsInitialModel) = copy(
            groupBrief = i.groupBrief,
            date = i.date,
            attendings = i.attendings
    )

    override fun stateReducer(vs: LessonDetailsViewState, change: PartialChange): LessonDetailsViewState {
        if (change !is LessonDetailsPartialChange) return super.stateReducer(vs, change)
                .let { viewState ->
                    if (change is LRPartialChange.InitialModelLoaded<*>)
                        viewState.copy(uploadingAttendingStateStatuses = emptyMap())
                    else
                        viewState
                }
        return when (change) {
            is LessonDetailsPartialChange.AuthedUserLoaded -> vs.copy(
                    authedUserAccountType = change.loginResult.accountType,
                    authedUserDetailsId = change.loginResult.detailsId
            )
            is LessonDetailsPartialChange.AttendingStateChanged -> vs.copy(
                    attendings = vs.attendings
                            ?.map {
                                if (it.id == change.attendingId)
                                    it.copy(state = change.state)
                                else
                                    it
                            }
            )
            is LessonDetailsPartialChange.UploadAttendingStateStarted -> vs.copy(
                    uploadingAttendingStateStatuses = vs.uploadingAttendingStateStatuses + Pair(change.attendingId, UploadStatus.UPLOADING)
            )
            is LessonDetailsPartialChange.UploadAttendingStateError -> vs.copy(
                    uploadingAttendingStateStatuses = vs.uploadingAttendingStateStatuses + Pair(change.attendingId, UploadStatus.ERROR)
            )
            is LessonDetailsPartialChange.UploadAttendingStateSuccess -> vs.copy(
                    uploadingAttendingStateStatuses = vs.uploadingAttendingStateStatuses + Pair(change.attendingId, UploadStatus.DONE)
            )
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
                        viewActions.onNext(LessonDetailsViewAction.ShowThereWillBeAttendingChangesCaching)
                    }
                }
                .toObservable()
                .subscribeToView()
    }
}