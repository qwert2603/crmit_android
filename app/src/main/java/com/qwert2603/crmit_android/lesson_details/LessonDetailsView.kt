package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.load_refresh.LRView
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import io.reactivex.Observable

interface LessonDetailsView : LRView<LessonDetailsViewState> {
    fun attendingStatesChanges(): Observable<SaveAttendingStateParams>
}