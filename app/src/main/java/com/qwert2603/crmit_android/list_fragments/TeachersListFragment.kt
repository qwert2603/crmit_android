package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity.Teacher
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeachersListFragment : EntitiesListFragment<Teacher>() {
    override val source = DiHolder.rest::getTeachersList
    override val dbDaoInterface: DaoInterface<Teacher> = DiHolder.teacherDaoInterface
    override val titleRes = R.string.title_teachers
    override val vhLayoutRes = R.layout.item_teacher
    override val entityPluralsRes = R.plurals.teachers
    override fun View.bindEntity(e: Teacher) {
        fio_TextView.setTextColor(resources.color(if (e.isAuthed()) R.color.colorAccent else android.R.color.black))
        fio_TextView.text = e.fio
        disabled_TextView.setVisible(!e.systemUser.enabled)
        fio_TextView.setStrike(!e.systemUser.enabled)
        fio_TextView.transitionName = "entity_name_${e.id}"
        login_TextView.text = e.systemUser.login
        phone_TextView.text = e.phone
        lessonsDoneCount_TextView.text = resources.getQuantityString(R.plurals.lessons_done, e.lessonsDoneCount, e.lessonsDoneCount)
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
                    DiHolder.router.navigateTo(Screen.TeacherDetails(DetailsScreenKey(
                            entityId = it.id,
                            entityName = it.fio,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.fio_TextView,
                            entityNameStrike = !it.systemUser.enabled,
                            entityNameColorAccent = it.isAuthed()
                    )))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun Teacher.isAuthed() = currentViewState.authedUserAccountType == AccountType.TEACHER && this.id == currentViewState.authedUserDetailsId
}