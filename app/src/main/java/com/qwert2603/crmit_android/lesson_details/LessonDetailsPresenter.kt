package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.andrlib.util.LogUtils
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.entity.Teacher
import com.qwert2603.crmit_android.entity.UploadStatus
import com.qwert2603.crmit_android.util.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class LessonDetailsPresenter(private val lessonId: Long)
    : LRPresenter<Any, LessonDetailsInitialModel, LessonDetailsViewState, LessonDetailsView>(DiHolder.uiSchedulerProvider) {

    override val canRefreshAtAll = false

    override val initialState = LessonDetailsViewState(EMPTY_LR_MODEL, null, null, null, null, emptyMap(), null, null)

    private val attendingStatesChangesIntent = intent { it.attendingStatesChanges() }.shareAfterViewSubscribed()

    private val saveAttendingsStateScheduler = Schedulers.from(Executors.newSingleThreadExecutor())

    private val daoInterface = DiHolder.attendingDao.wrap(lessonId)

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
                                    daoInterface.getItem(params.attendingId)
                                            ?.also { daoInterface.saveItem(it.copy(state = params.attendingState)) }
                                }
                                .toSingleDefault<LessonDetailsPartialChange>(LessonDetailsPartialChange.UploadAttendingStateSuccess(params.attendingId))
                                .onErrorReturnItem(LessonDetailsPartialChange.UploadAttendingStateError(params.attendingId))
                                .toObservable()
                                .subscribeOn(saveAttendingsStateScheduler)
                                .startWith(LessonDetailsPartialChange.UploadAttendingStateStarted(params.attendingId))
                                .takeUntil(attendingStatesChangesIntent.filter { it.attendingId == params.attendingId })
                    }
    ))

    private fun getAttendingsFromServer() = DiHolder.rest
            .getAttendingsOfLesson(lessonId)
            .doOnSuccess {
                daoInterface.deleteAllItems()
                daoInterface.addItems(it)
            }
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    // todo: load groupBrief / teacher in LessonsInGroupPresenter.
    private fun Single<List<Attending>>.toInitialModel(): Single<LessonDetailsInitialModel> = this
            .flatMap { attendings ->
                val lesson = DiHolder.lessonDao.getItem(lessonId)
                if (lesson != null) {
                    val groupBriefSource = DiHolder.rest
                            .getGroupDetails(lesson.groupId)
                            .doOnSuccess { DiHolder.groupFullDaoInterface.saveItem(it) }
                            .map { it.toGroupBrief() }
                            .doOnSuccess { DiHolder.groupBriefCustomOrderDao.saveItem(it) }
                            .map { it.wrap() }
                            .onErrorReturnItem((
                                    DiHolder.groupBriefCustomOrderDao.getItem(lesson.groupId)
                                            ?: DiHolder.groupFullDaoInterface.getItem(lesson.groupId)?.toGroupBrief()
                                    ).wrap()
                            )

                    val teacherSource: Single<Wrapper<Teacher>> = DiHolder.rest
                            .getTeacherDetails(lesson.teacherId)
                            .doOnSuccess { DiHolder.teacherDaoInterface.saveItem(it) }
                            .map { it.wrap() }
                            .onErrorReturnItem(DiHolder.teacherDaoInterface.getItem(lesson.teacherId).wrap())

                    Single
                            .zip(
                                    groupBriefSource,
                                    teacherSource,
                                    BiFunction { groupBrief, teacher ->
                                        LessonDetailsInitialModel(
                                                groupBrief = groupBrief.t,
                                                teacher = teacher.t,
                                                date = lesson.date,
                                                attendings = attendings
                                        )
                                    }
                            )
                } else {
                    Single.just(LessonDetailsInitialModel(
                            groupBrief = null,
                            teacher = null,
                            date = null,
                            attendings = attendings
                    ))
                }
            }

    override fun initialModelSingle(additionalKey: Any): Single<LessonDetailsInitialModel> = getAttendingsFromServer()
            .onErrorResumeNext { t ->
                LogUtils.e("LessonDetailsPresenter getAttendingsFromServer", t)
                Single
                        .fromCallable { daoInterface.getItems() }
                        .flatMap {
                            if (it.isNotEmpty()) {
                                Single.just(it)
                            } else {
                                Single.error(NoCacheException())
                            }
                        }
                        .subscribeOn(DiHolder.modelSchedulersProvider.io)
            }
            .toInitialModel()

    override fun initialModelSingleRefresh(additionalKey: Any): Single<LessonDetailsInitialModel> = getAttendingsFromServer()
            .toInitialModel()

    override fun LessonDetailsViewState.applyInitialModel(i: LessonDetailsInitialModel) = copy(
            groupBrief = i.groupBrief,
            teacher = i.teacher,
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
                    if (it.groupBrief == null || it.date == null) return@doOnNext
                    viewActions.onNext(LessonDetailsViewAction.NavigateToPayments(it.groupBrief.id, it.date.toMonthNumber()))
                }
                .subscribeToView()
    }
}