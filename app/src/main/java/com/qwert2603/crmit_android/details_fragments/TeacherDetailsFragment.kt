package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Teacher
import com.qwert2603.crmit_android.entity_details.*
import com.qwert2603.crmit_android.util.toLastSeenString

@FragmentWithArgs
class TeacherDetailsFragment : EntityDetailsFragment<Teacher>() {

    override val source = DiHolder.rest::getTeacherDetails

    override val dbDaoInterface = DiHolder.teacherDaoInterface

    override fun Teacher.entityName() = fio

    override fun Teacher.entityNameStrike() = !systemUser.enabled

    override fun EntityDetailsViewState<Teacher>.entityNameColorAccent() = authedUserAccountType == AccountType.TEACHER && entity != null && entity.id == authedUserDetailsId

    override fun Teacher.toDetailsList() = listOf(
            EntityDetailsSystemInfo(systemUser.enabled, true),
            EntityDetailsField(R.string.detailsField_login, systemUser.login),
            EntityDetailsField(R.string.detailsField_systemRoleName, systemUser.systemRoleName),
            EntityDetailsField(R.string.detailsField_lastSeen, systemUser.toLastSeenString(resources)),
            EntityDetailsField(R.string.detailsField_phone, phone, R.drawable.ic_local_phone_black_24dp),
            EntityDetailsField(R.string.detailsField_lessonsDoneCount, lessonsDoneCount.toString()),
            EntityDetailsGroupsList(groups = groups, showTeacherFio = false)
    )
}