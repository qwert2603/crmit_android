package com.qwert2603.crmit_android.list_fragments

import android.annotation.SuppressLint
import android.view.View
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.StudentBrief
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.item_student.view.*

class StudentsListFragment : EntitiesListFragment<StudentBrief>() {
    override val source = DiHolder.rest::getStudentsList
    override val titleRes = R.string.title_students
    override val vhLayoutRes = R.layout.item_student
    override val entityPluralsRes = R.plurals.students
    override val pageSize = 1
    override fun View.bindEntity(e: StudentBrief) {
        fio_TextView.text = e.fio
        disabled_TextView.setVisible(!e.systemUser.enabled)
        fio_TextView.setStrike(!e.systemUser.enabled)
        notFilled_TextView.setVisible(!e.filled)
        login_TextView.text = e.systemUser.login
        @SuppressLint("SetTextI18n")
        phone_TextView.text = "${e.contactPhoneNumber} (${e.contactPhoneWho})"
        groups_TextView.setVisible(e.groups.isNotEmpty())
        if (e.groups.isNotEmpty()) {
            groups_TextView.text = e.groups
                    .map { "* ${it.name}" }
                    .reduce { acc, s -> "$acc\n$s" }
        }
    }
}