package com.qwert2603.crmit_android.details_fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.entity_details.EntityDetailsField
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.entity_details.EntityDetailsViewState
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class GroupDetailsFragment : EntityDetailsFragment<GroupFull>() {
    data class Key(
            val groupId: Long,
            val groupName: String,
            val groupNameTextView: TextView
    )

    @Arg
    override var entityId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var groupName: String

    override val source = DiHolder.rest::getGroupDetails

    override val dbDao = DiHolder.groupFullDao

    override fun GroupFull.toDetailsList() = kotlin.collections.listOf(
            EntityDetailsField(R.string.detailsField_section, sectionName),
            EntityDetailsField(R.string.detailsField_teacher, teacherFio),
            EntityDetailsField(R.string.detailsField_startMonth, startMonth.toMonthString()),
            EntityDetailsField(R.string.detailsField_endMonth, endMonth.toMonthString()),
            EntityDetailsField(R.string.detailsField_studentsCount, studentsCount.toString()),
            EntityDetailsField(R.string.detailsField_lessonsDoneCount, lessonsDoneCount.toString())
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.title = groupName
        toolbarTitleTextView.transitionName = "group_name_$entityId"

        super.onViewCreated(view, savedInstanceState)
    }

    override fun render(vs: EntityDetailsViewState<GroupFull>) {
        super.render(vs)

        vs.entity?.let { toolbarTitleTextView.text = it.name }
    }

    private fun Int.toMonthString() = "${this / 12 + 2017} ${resources.getStringArray(R.array.month_names)[this % 12]}"
}