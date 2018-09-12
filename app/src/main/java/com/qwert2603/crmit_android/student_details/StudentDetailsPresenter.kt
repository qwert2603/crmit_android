package com.qwert2603.crmit_android.student_details

import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.andrlib.base.mvi.load_refresh.LRPresenter
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.StudentFull
import io.reactivex.Observable
import io.reactivex.Single

class StudentDetailsPresenter(private val studentId: Long) : LRPresenter<Any, StudentFull, StudentDetailsViewState, StudentDetailsView>(DiHolder.uiSchedulerProvider) {
    override val initialState = StudentDetailsViewState(EMPTY_LR_MODEL, null)

    override val partialChanges: Observable<PartialChange> = loadRefreshPartialChanges()

    override fun initialModelSingle(additionalKey: Any): Single<StudentFull> = DiHolder.rest
            .getStudentDetails(studentId)
            .subscribeOn(DiHolder.modelSchedulersProvider.io)

    override fun StudentDetailsViewState.applyInitialModel(i: StudentFull) = copy(studentFull = i)
}