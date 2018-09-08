package com.qwert2603.crmit_android.list_fragments

import android.view.View
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.Teacher
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeachersListFragment : EntitiesListFragment<Teacher>() {
    override val source = DiHolder.rest::getTeachersList
    override val titleRes = R.string.title_teachers
    override val vhLayoutRes = R.layout.item_teacher
    override val entityPluralsRes = R.plurals.teachers
    override fun View.bindEntity(e: Teacher) {
        fio_TextView.text = e.fio
        login_TextView.text = e.systemUser.login
        phone_TextView.text = e.phone
        lessonsCount_TextView.text = resources.getQuantityString(R.plurals.lessons, e.lessonsCount, e.lessonsCount)
        groups_TextView.setVisible(e.groups.isNotEmpty())
        if (e.groups.isNotEmpty()) {
            groups_TextView.text = e.groups
                    .map { "* ${it.name}" }
                    .reduce { acc, s -> "$acc\n$s" }
        }
    }
}