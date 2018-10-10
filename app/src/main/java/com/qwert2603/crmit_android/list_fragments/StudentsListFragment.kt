package com.qwert2603.crmit_android.list_fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.StudentBrief
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.item_student.view.*

class StudentsListFragment : EntitiesListFragment<StudentBrief>() {

    override val source = DiHolder.rest::getStudentsList

    override val dbDaoInterface: DaoInterface<StudentBrief> = DiHolder.studentBriefDaoInterface

    override val titleRes = R.string.title_students

    override val vhLayoutRes = R.layout.item_student

    override val entityPluralsRes = R.plurals.students

    override val pageSize = 20

    override fun View.bindEntity(e: StudentBrief) {
        fio_TextView.text = e.fio
        fio_TextView.transitionName = "entity_name_${e.id}"
        disabled_TextView.setVisible(!e.systemUser.enabled)
        fio_TextView.setStrike(!e.systemUser.enabled)
        notFilled_TextView.setVisible(!e.filled)
        login_TextView.text = e.systemUser.login
        @SuppressLint("SetTextI18n")
        phone_TextView.text = "${e.contactPhoneNumber} (${e.contactPhoneWho})"
        school_TextView.text = getString(R.string.student_school_format, e.schoolName, e.grade, e.shift)
        lessonsAttendedCount_TextView.text = resources.getQuantityString(R.plurals.lessons_attended, e.lessonsAttendedCount, e.lessonsAttendedCount)
        groups_TextView.setVisible(e.groups.isNotEmpty())
        if (e.groups.isNotEmpty()) {
            groups_TextView.text = e.groups
                    .map { "* ${it.name}" }
                    .reduce { acc, s -> "$acc\n$s" }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(Screen.StudentDetails(DetailsScreenKey(
                            entityId = it.id,
                            entityName = it.fio,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.fio_TextView,
                            entityNameStrike = !it.systemUser.enabled
                    )))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }
}