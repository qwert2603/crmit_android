package com.qwert2603.crmit_android.lesson_details

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.navigation.ScreenKey
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import com.qwert2603.crmit_android.util.toShowingDate
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_lesson_details.*
import kotlinx.android.synthetic.main.item_entity_details_field.view.*
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class LessonDetailsFragment : LRFragment<LessonDetailsViewState, LessonDetailsView, LessonDetailsPresenter>(), LessonDetailsView {

    @Arg
    var lessonId: Long = IdentifiableLong.NO_ID

    override fun createPresenter() = LessonDetailsPresenter(lessonId)

    override fun loadRefreshPanel(): LoadRefreshPanel = lessonDetails_LRPanelImpl

    override fun viewForSnackbar(): View? = lessonDetails_CoordinatorLayout

    private val adapter = AttendingsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_lesson_details)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_lesson_default)

        group_DetailsField.fieldName_TextView.setText(R.string.detailsField_group)
        teacher_DetailsField.fieldName_TextView.setText(R.string.detailsField_teacher)

        group_DetailsField.fieldValue_TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_group_black_24dp,
                0,
                R.drawable.ic_navigate_next_black_24dp,
                0
        )
        teacher_DetailsField.fieldValue_TextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_person_black_24dp,
                0,
                R.drawable.ic_navigate_next_black_24dp,
                0
        )

        group_DetailsField.setOnClickListener {
            val groupBrief = currentViewState.groupBrief ?: return@setOnClickListener
            DiHolder.router.navigateTo(ScreenKey.GROUP_DETAILS.name, EntityDetailsFragment.Key(
                    entityId = groupBrief.id,
                    entityName = groupBrief.name
            ))
        }

        teacher_DetailsField.setOnClickListener {
            val groupBrief = currentViewState.groupBrief ?: return@setOnClickListener
            DiHolder.router.navigateTo(ScreenKey.TEACHER_DETAILS.name, EntityDetailsFragment.Key(
                    entityId = groupBrief.teacherId,
                    entityName = groupBrief.teacherFio
            ))
        }

        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(ScreenKey.STUDENT_DETAILS.name, EntityDetailsFragment.Key(
                            entityId = it.studentId,
                            entityName = it.studentFio
                    ))
                }
                .disposeOnDestroyView()

        attendings_RecyclerView.itemAnimator = null
        attendings_RecyclerView.adapter = adapter

        super.onViewCreated(view, savedInstanceState)
    }

    override fun attendingStatesChanges(): Observable<SaveAttendingStateParams> = adapter.attendingStateChanges

    override fun render(vs: LessonDetailsViewState) {
        super.render(vs)

        vs.date?.let { toolbar.title = getString(R.string.title_lesson_format, it.toShowingDate()) }

        group_DetailsField.setVisible(vs.groupBrief != null)
        teacher_DetailsField.setVisible(vs.groupBrief != null)
        if (vs.groupBrief != null) {
            group_DetailsField.fieldValue_TextView.text = vs.groupBrief.name

            teacher_DetailsField.fieldValue_TextView.text = vs.groupBrief.teacherFio
            teacher_DetailsField.fieldValue_TextView.setTextColor(resources.color(
                    if (vs.authedUserAccountType == AccountType.TEACHER && vs.authedUserDetailsId == vs.groupBrief.teacherId)
                        R.color.colorAccent
                    else
                        android.R.color.black
            ))
        }

        adapter.uploadStatuses = vs.uploadingAttendingStateStatuses
        adapter.adapterList = BaseRecyclerViewAdapter.AdapterList(vs.attendings ?: emptyList())
    }

    override fun executeAction(va: ViewAction) {
        if (va !is LessonDetailsViewAction) return super.executeAction(va)
        when (va) {
            LessonDetailsViewAction.ShowingCachedData -> Snackbar.make(lessonDetails_CoordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
        }.also { }
    }
}