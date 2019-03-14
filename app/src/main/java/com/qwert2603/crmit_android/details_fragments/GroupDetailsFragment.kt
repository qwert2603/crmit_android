package com.qwert2603.crmit_android.details_fragments

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.BotAccountIsNotSupportedException
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.toMonthString
import com.qwert2603.crmit_android.util.toPointedString

@FragmentWithArgs
class GroupDetailsFragment : EntityDetailsFragment<GroupFull>() {

    override val source = DiHolder.rest::getGroupDetails

    override val dbDaoInterface = DiHolder.groupFullDaoInterface

    override fun GroupFull.entityName() = name

    private val daysOfWeekNames by lazy { resources.getStringArray(R.array.days_of_week_names) }

    override fun GroupFull.toDetailsList() = listOfNotNull(
            EntityDetailsField(
                    fieldTitleStringRes = R.string.detailsField_teacher,
                    fieldValue = teacherFio,
                    iconDrawableRes = R.drawable.ic_person_black_24dp,
                    textColorRes = if (currentViewState.authedUserAccountType == AccountType.TEACHER && currentViewState.authedUserDetailsId == teacherId)
                        R.color.colorAccent
                    else
                        android.R.color.black,
                    clickCallback = { DiHolder.router.navigateTo(Screen.TeacherDetails(DetailsScreenKey(teacherId, teacherFio))) }
            ),
            EntityDetailsField(R.string.detailsField_section, sectionName, R.drawable.ic_group_black_24dp) {
                DiHolder.router.navigateTo(Screen.SectionDetails(DetailsScreenKey(sectionId, sectionName)))
            },
            EntityDetailsField(R.string.detailsField_startMonth, startMonth.toMonthString(resources)),
            EntityDetailsField(R.string.detailsField_endMonth, endMonth.toMonthString(resources)),
            EntityDetailsField(R.string.detailsField_studentsCount, studentsCount.toString(), R.drawable.ic_group_black_24dp) {
                DiHolder.router.navigateTo(Screen.StudentsInGroup(id, name, startMonth, endMonth))
            },
            EntityDetailsField(R.string.detailsField_lessonsDoneCount, lessonsDoneCount.toString(), R.drawable.ic_date_range_black_24dp) {
                DiHolder.router.navigateTo(Screen.LessonsInGroupList(id, name))
            },
            EntityDetailsField(R.string.detailsField_payments, getString(R.string.detailsField_paymentsInfo), R.drawable.ic_ruble) {
                DiHolder.router.navigateTo(Screen.PaymentsInGroup(id))
            }.takeIf {
                when (currentViewState.authedUserAccountType) {
                    AccountType.MASTER -> true
                    AccountType.TEACHER -> currentViewState.authedUserDetailsId == teacherId
                    AccountType.DEVELOPER -> true
                    AccountType.BOT -> throw BotAccountIsNotSupportedException()
                    null -> false
                }
            },
            EntityDetailsField(
                    fieldTitleStringRes = R.string.detailsField_sumNotConfirmed,
                    fieldValue = getString(R.string.price_format, sumNotConfirmed.toPointedString()),
                    iconDrawableRes = R.drawable.ic_summa
            ),
            EntityDetailsField(
                    fieldTitleStringRes = R.string.detailsField_schedule,
                    fieldValue = schedule
                            .takeIf { it.isNotEmpty() }
                            ?.map { "${daysOfWeekNames[it.dayOfWeek]} ${it.time ?: ""}" }
                            ?.reduce { acc, s -> "$acc\n$s" }
                            ?: getString(R.string.text_no_schedule),
                    iconDrawableRes = R.drawable.ic_schedule_black_24dp
            )
    )
}