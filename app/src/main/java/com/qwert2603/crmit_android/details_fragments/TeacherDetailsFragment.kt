package com.qwert2603.crmit_android.details_fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.Teacher
import com.qwert2603.crmit_android.entity_details.*
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class TeacherDetailsFragment : EntityDetailsFragment<Teacher>() {

    data class Key(
            val teacherId: Long,
            val teacherFio: String,
            val systemUserEnabled: Boolean,
            val teacherFioTextView: TextView
    )

    @Arg
    override var entityId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var teacherFio: String

    @Arg
    var systemUserEnabled: Boolean = true

    override val source = DiHolder.rest::getTeacherDetails

    override val dbDao = DiHolder.teacherDao

    override fun Teacher.toDetailsList() = listOf(
            EntityDetailsSystemInfo(systemUser.enabled, true),
            EntityDetailsField(R.string.detailsField_login, systemUser.login),
            EntityDetailsField(R.string.detailsField_phone, phone, R.drawable.ic_local_phone_black_24dp),
            EntityDetailsField(R.string.detailsField_lessonsDoneCount, lessonsDoneCount.toString()),
            EntityDetailsGroupsList(groups = groups, showTeacherFio = false)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.title = teacherFio
        toolbarTitleTextView.transitionName = "teacher_fio_$entityId"
        toolbarTitleTextView.setStrike(!systemUserEnabled)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun render(vs: EntityDetailsViewState<Teacher>) {
        super.render(vs)

        vs.entity?.let {
            toolbarTitleTextView.text = it.fio
            toolbarTitleTextView.setStrike(!it.systemUser.enabled)
        }
    }
}