package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Teacher
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsGroupsList
import com.qwert2603.crmit_android.entity_details.EntityDetailsSystemInfo

@FragmentWithArgs
class TeacherDetailsFragment : EntityDetailsFragment<Teacher>() {

    override val source = DiHolder.rest::getTeacherDetails

    override val dbDao = DiHolder.teacherDao

    override fun Teacher.entityName() = fio

    override fun Teacher.entityNameStrike() = !systemUser.enabled

    override fun Teacher.toDetailsList() = listOf(
            EntityDetailsSystemInfo(systemUser.enabled, true),
            EntityDetailsField(R.string.detailsField_login, systemUser.login),
            EntityDetailsField(R.string.detailsField_phone, phone, R.drawable.ic_local_phone_black_24dp),
            EntityDetailsField(R.string.detailsField_lessonsDoneCount, lessonsDoneCount.toString()),
            EntityDetailsGroupsList(groups = groups, showTeacherFio = false)
    )
}