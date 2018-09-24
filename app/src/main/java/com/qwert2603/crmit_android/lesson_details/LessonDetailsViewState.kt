package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Attending
import com.qwert2603.crmit_android.entity.GroupBrief

@GenerateLRChanger
data class LessonDetailsViewState(
        override val lrModel: LRModel,
        val groupBrief: GroupBrief?,
        val date: String?,
        val attendings: List<Attending>?,
        val uploadingAttendingStateStatuses: Map<Long, UploadStatus>,
        val authedUserAccountType: AccountType?,
        val authedUserDetailsId: Long?
) : LRViewState