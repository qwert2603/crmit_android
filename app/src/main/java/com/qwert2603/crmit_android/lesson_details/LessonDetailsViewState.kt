package com.qwert2603.crmit_android.lesson_details

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.*

@GenerateLRChanger
data class LessonDetailsViewState(
        override val lrModel: LRModel,
        val groupBrief: GroupBrief?,
        val teacher: Teacher?,
        val date: String?,
        val attendings: List<Attending>?,
        val uploadingAttendingStateStatuses: Map<Long, UploadStatus>,
        val authedUserAccountType: AccountType?,
        val authedUserDetailsId: Long?
) : LRViewState {
    fun isUserCanWriteGroup() = when (authedUserAccountType) {
        AccountType.MASTER -> true
        AccountType.TEACHER -> authedUserDetailsId != null && authedUserDetailsId == groupBrief?.teacherId
        AccountType.DEVELOPER -> true
        AccountType.BOT -> throw BotAccountIsNotSupportedException()
        null -> false
    }

    fun isNavigateToPaymentsVisible() = isUserCanWriteGroup() && groupBrief != null && date != null
}