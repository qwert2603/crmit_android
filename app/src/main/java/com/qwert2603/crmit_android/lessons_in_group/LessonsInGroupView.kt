package com.qwert2603.crmit_android.lessons_in_group

import com.qwert2603.andrlib.base.mvi.load_refresh.LRView
import io.reactivex.Observable

interface LessonsInGroupView : LRView<LessonsInGroupViewState> {
    fun dateSelected(): Observable<String>
}