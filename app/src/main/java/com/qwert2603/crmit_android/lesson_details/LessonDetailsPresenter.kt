package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.UploadStatus
import com.qwert2603.crmit_android.util.NoCacheException
import com.qwert2603.crmit_android.util.secondOfTwo
import com.qwert2603.crmit_android.util.toMonthNumber
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class LessonDetailsPresenter(private val lessonId: Long, canRefresh: Boolean)
    : LRPresenter<Any, LessonDetailsInitialModel, LessonDetailsViewState, LessonDetailsView>(DiHolder.uiSchedulerProvider) {

    override val canRefreshAtAll = canRefresh

    override val initialState = LessonDetailsViewState(EMPTY_LR_MODEL, null, null, null, null, emptyMap(), null, null)

    private val attendingStatesChangesIntent = intent { it.attendingStatesChanges() }.shareAfterViewSubscribed()

    private val saveAttendingsStateScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val attendingsDaoInterface = DiHolder.attendingDao.wrap(lessonId)

    override val partialChanges: Observable<PartialChange> = Observable.merge(listOf(
            loadRefreshPartialChanges(),
            loadIntent
                    .switchMapSingle { DiHolder.userSettingsRepo.getLoginResultOrMoveToLogin() }
                    .map { LessonDetailsPartialChange.AuthedUserLoaded(it) },
            attendingStatesChangesIntent
                    .map { LessonDetailsPartialChange.AttendingStateChanged(it.attendingId, it.attendingState) },
            attendingStatesChangesIntent
                    .flatMap { params ->
                        DiHolder.rest.saveAttendingState(params)
                                .doOnComplete {
                                    attendingsDaoInterface.getItem(params.attendingId)
                                            ?.also { attendingsDaoInterface.saveItem(it.copy(state = params.attendingState)) }
                                }
                                .toSingleDefault<LessonDetailsPartialChange>(LessonDetailsPartialChange.UploadAttendingStateSuccess(params.attendingId))
                                .onErrorReturnItem(LessonDetailsPartialChange.UploadAttendingStateError(params.attendingId))
                                .toObservable()
                                .subscribeOn(saveAttendingsStateScheduler)
                                .startWith(LessonDetailsPartialChange.UploadAttendingStateStarted(params.attendingId))
                                .takeUntil(attendingStatesChangesIntent.filter { it.attendingId == params.attendingId })
                    }
    ))

    override fun initialModelSingle(additionalKey: Any): Single<LessonDetailsInitialModel> = initialModelSingleRefresh(additionalKey)
            .onErrorResumeNext { t ->
                LogUtils.e("LessonDetailsPresenter getAttendingsFromServer", t)
//                viewActions.onNext(CabinetViewAction.ShowingCachedData) todo
                Single
                        .fromCallable { attendingsDaoInterface.getItems() }
                        .flatMap { attendings ->
                            if (attendings.isNotEmpty()) {
                                val lesson = DiHolder.lessonDao.getItem(lessonId)
                                if (lesson != null) {
                                    Single.just(LessonDetailsInitialModel(
                                            groupBrief = DiHolder.groupBriefCustomOrderDao.getItem(lesson.groupId)
                                                    ?: DiHolder.groupFullDaoInterface.getItem(lesson.groupId)?.toGroupBrief(),
                                            teacher = DiHolder.teacherDaoInterface.getItem(lesson.teacherId),
                                            lesson = lesson,
                                            attendings = attendings
                                    ))
                                } else {
                                    Single.just(LessonDetailsInitialModel(
                                            groupBrief = null,
                                            teacher = null,
                                            lesson = null,
                                            attendings = attendings
                                    ))
                                }
                            } else {
                                Single.error(NoCacheException())
                            }
                        }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }

    override fun initialModelSingleRefresh(additionalKey: Any): Single<LessonDetailsInitialModel> = DiHolder.rest
            .getLessonDetails(lessonId)
            .doOnSuccess {
                DiHolder.teacherDaoInterface.saveItem(it.teacher)
                DiHolder.groupBriefCustomOrderDao.saveItem(it.group)
                DiHolder.lessonDao.saveItem(it.lesson)
                attendingsDaoInterface.deleteAllItems()
                attendingsDaoInterface.addItems(it.attendings)
            }
            .map {
                LessonDetailsInitialModel(
                        groupBrief = it.group,
                        teacher = it.teacher,
                        lesson = it.lesson,
                        attendings = it.attendings
                )
            }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun LessonDetailsViewState.applyInitialModel(i: LessonDetailsInitialModel) = copy(
            groupBrief = i.groupBrief,
            teacher = i.teacher,
            lesson = i.lesson,
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
                    attendings = vs.attendings?.map {
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

        intent { it.navigateToPaymentsClicks() }
                .withLatestFrom(viewStateObservable, secondOfTwo())
                .doOnNext {
                    if (it.groupBrief == null || it.lesson == null) return@doOnNext
                    viewActions.onNext(LessonDetailsViewAction.NavigateToPayments(it.groupBrief.id, it.lesson.date.toMonthNumber()))
                }
                .subscribeToView()
    }
}