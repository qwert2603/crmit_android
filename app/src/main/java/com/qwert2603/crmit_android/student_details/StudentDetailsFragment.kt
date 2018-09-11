package com.qwert2603.crmit_android.student_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import kotlinx.android.synthetic.main.fragment_student_details.*
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class StudentDetailsFragment : LRFragment<StudentDetailsViewState, StudentDetailsView, StudentDetailsPresenter>(), StudentDetailsView {

    @Arg
    var studentId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var studentFio: String

    override fun createPresenter() = StudentDetailsPresenter(studentId)

    override fun loadRefreshPanel(): LoadRefreshPanel = studentDetails_LRPanelImpl

    override fun viewForSnackbar(): View? = studentDetails_CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_student_details)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = studentFio
        toolbar.getChildAt(0).transitionName = "student_fio_$studentId"
    }

    override fun render(vs: StudentDetailsViewState) {
        super.render(vs)

        vs.studentFull?.fio?.let { toolbar.title = it }
        vs.studentFull?.also {
            login_TextView.text = it.toString()
        }
    }
}