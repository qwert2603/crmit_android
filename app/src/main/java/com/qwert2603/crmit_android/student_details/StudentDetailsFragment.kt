package com.qwert2603.crmit_android.student_details

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewAnimator
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.showIfNotYet
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.entity.Parent
import com.qwert2603.crmit_android.util.ConditionDividerDecoration
import com.qwert2603.crmit_android.util.setStrike
import kotlinx.android.synthetic.main.fragment_student_details.*
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class StudentDetailsFragment : LRFragment<StudentDetailsViewState, StudentDetailsView, StudentDetailsPresenter>(), StudentDetailsView {

    @Arg
    var studentId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var studentFio: String

    @Arg
    var systemUserEnabled: Boolean = true

    private val adapter = StudentDetailsAdapter()

    override fun createPresenter() = StudentDetailsPresenter(studentId)

    override fun loadRefreshPanel(): LoadRefreshPanel = studentDetails_LRPanelImpl

    override fun viewForSnackbar(): View? = studentDetails_CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_student_details)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = studentFio
        toolbarTitleTextView().transitionName = "student_fio_$studentId"
        toolbarTitleTextView().setStrike(!systemUserEnabled)

        view.findViewById<ViewAnimator>(R.id.list_ViewAnimator).showIfNotYet(2)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ConditionDividerDecoration(requireContext()) { _, vh -> vh is StudentDetailsViewHolder })
    }

    override fun render(vs: StudentDetailsViewState) {
        super.render(vs)

        vs.studentFull?.let {
            toolbarTitleTextView().text = it.fio
            toolbarTitleTextView().setStrike(!it.systemUser.enabled)
        }

        renderIfChanged({ studentFull }) { student ->
            if (student == null) return@renderIfChanged
            adapter.adapterList = BaseRecyclerViewAdapter.AdapterList(listOfNotNull(
                    StudentDetailsSystemInfo(student.systemUser.enabled, student.filled),
                    StudentDetailsField(R.string.student_details_field_login, student.systemUser.login, R.drawable.ic_person_black_24dp),
                    StudentDetailsField(R.string.student_details_field_lessonsAttendedCount, student.lessonsAttendedCount.toString()),
                    StudentDetailsField(R.string.student_details_field_birthDate, student.showingBirthDate()),
                    StudentDetailsField(R.string.student_details_field_birthPlace, student.birthPlace),
                    StudentDetailsField(R.string.student_details_field_registrationPlace, student.registrationPlace),
                    StudentDetailsField(R.string.student_details_field_actualAddress, student.actualAddress),
                    student.additionalInfo?.takeIf { it.isNotEmpty() }?.let { StudentDetailsField(R.string.student_details_field_additionalInfo, it) },
                    student.knownFrom?.takeIf { it.isNotEmpty() }?.let { StudentDetailsField(R.string.student_details_field_knownFrom, it) },
                    StudentDetailsField(R.string.student_details_field_school, getString(R.string.student_school_format, student.school.name, student.grade, student.shift)),
                    student.phone?.let { StudentDetailsField(R.string.student_details_field_phone, it, R.drawable.ic_local_phone_black_24dp) },
                    StudentDetailsField(R.string.student_details_field_contactPhone, "${student.contactPhoneNumber} (${student.contactPhoneWho})", R.drawable.ic_local_phone_black_24dp),
                    StudentDetailsField(R.string.student_details_field_citizenshipName, student.citizenshipName),
                    student.mother?.let { StudentDetailsField(R.string.student_details_field_mother, it.toTextFieldValue()) },
                    student.father?.let { StudentDetailsField(R.string.student_details_field_father, it.toTextFieldValue()) },
                    StudentDetailsField(R.string.student_details_field_groups, student.groups.toTextFieldValue())
            ))
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is StudentDetailsViewAction) return super.executeAction(va)
        when (va) {
            StudentDetailsViewAction.ShowingCachedData -> Snackbar.make(studentDetails_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
        }.also { }
    }

    private fun toolbarTitleTextView(): TextView = toolbar.getChildAt(0) as TextView

    private fun Parent.toTextFieldValue() = listOf(
            R.string.student_details_field_fio to fio,
            R.string.student_details_field_phone to phone,
            R.string.student_details_field_address to address,
            R.string.student_details_field_email to email,
            R.string.student_details_field_vkLink to vkLink,
            R.string.student_details_field_homePhone to homePhone,
            R.string.student_details_field_notificationTypesString to notificationTypesString.takeIf { it.isNotEmpty() }?.reduce { acc, s -> "$acc / $s" }
    )
            .filter { it.second != null }
            .map { "${getString(it.first)}: ${it.second}" }
            .reduce { acc, s -> "$acc\n$s" }

    private fun List<GroupBrief>.toTextFieldValue() =
            if (isEmpty()) getString(R.string.text_no_groups)
            else this
                    .map { "* ${it.name} (${it.teacherFio})" }
                    .reduce { acc, s -> "$acc\n$s" }
}