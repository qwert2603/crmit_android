package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.navigation.ScreenKey

@FragmentWithArgs
class GroupDetailsFragment : EntityDetailsFragment<GroupFull>() {

    override val source = DiHolder.rest::getGroupDetails

    override val dbDao = DiHolder.groupFullDao

    override fun GroupFull.entityName() = name

    override fun GroupFull.toDetailsList() = kotlin.collections.listOf(
            EntityDetailsField(R.string.detailsField_teacher, teacherFio, R.drawable.ic_person_black_24dp) {
                DiHolder.router.navigateTo(ScreenKey.TEACHER_DETAILS.name, EntityDetailsFragment.Key(teacherId, teacherFio))
            },
            EntityDetailsField(R.string.detailsField_section, sectionName, R.drawable.ic_group_black_24dp) {
                DiHolder.router.navigateTo(ScreenKey.SECTION_DETAILS.name, EntityDetailsFragment.Key(sectionId, sectionName))
            },
            EntityDetailsField(R.string.detailsField_startMonth, startMonth.toMonthString()),
            EntityDetailsField(R.string.detailsField_endMonth, endMonth.toMonthString()),
            EntityDetailsField(R.string.detailsField_studentsCount, studentsCount.toString()),
            EntityDetailsField(R.string.detailsField_lessonsDoneCount, lessonsDoneCount.toString())
    )

    private fun Int.toMonthString() = "${this / 12 + 2017} ${resources.getStringArray(R.array.month_names)[this % 12]}"
}