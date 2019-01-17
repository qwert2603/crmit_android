package com.qwert2603.crmit_android.lesson_details

import android.os.Bundle
import android.view.*
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewAdapter
import com.qwert2603.andrlib.base.recyclerview.page_list_item.AllItemsLoaded
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.lessons_in_group.ParentLessonsFragment
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.rest.params.SaveAttendingStateParams
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_lesson_details.*
import kotlinx.android.synthetic.main.item_entity_details_field.view.*

@FragmentWithArgs
class LessonDetailsFragment : LRFragment<LessonDetailsViewState, LessonDetailsView, LessonDetailsPresenter>(), LessonDetailsView {

    @Arg
    var lessonId: Long = IdentifiableLong.NO_ID

    override fun createPresenter() = LessonDetailsPresenter(lessonId)

    override fun loadRefreshPanel(): LoadRefreshPanel = lessonDetails_LRPanelImpl

    private val adapter = AttendingsAdapter()

    private val navigateToPaymentsClicks = PublishSubject.create<Any>()

    private var paymentsMenuItem: MenuItem? = null
        set(value) {
            field = value
            field?.isVisible = currentViewState.isNavigateToPaymentsVisible()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_lesson_details)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            DiHolder.router.navigateTo(Screen.GroupDetails(DetailsScreenKey(
                    entityId = groupBrief.id,
                    entityName = groupBrief.name
            )))
        }

        teacher_DetailsField.setOnClickListener {
            val teacher = currentViewState.teacher ?: return@setOnClickListener
            DiHolder.router.navigateTo(Screen.TeacherDetails(DetailsScreenKey(
                    entityId = teacher.id,
                    entityName = teacher.fio
            )))
        }

        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(Screen.StudentDetails(DetailsScreenKey(
                            entityId = it.studentId,
                            entityName = it.studentFio
                    )))
                }
                .disposeOnDestroyView()

        attendings_RecyclerView.itemAnimator = null
        attendings_RecyclerView.adapter = adapter

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.lesson, menu)
        val menuItem = menu.findItem(R.id.payments)
        menuItem.setOnMenuItemClickListener { navigateToPaymentsClicks.onNext(Any());true }
        paymentsMenuItem = menuItem
    }

    override fun retry(): Observable<Any> = super.retry()
            .doOnNext { (parentFragment as ParentLessonsFragment).onRetryDateClicked() }
            .filter { false }

    override fun attendingStatesChanges(): Observable<SaveAttendingStateParams> = adapter.attendingStateChanges

    override fun navigateToPaymentsClicks(): Observable<Any> = navigateToPaymentsClicks

    override fun render(vs: LessonDetailsViewState) {
        super.render(vs)

        group_DetailsField.setVisible(vs.groupBrief != null)
        if (vs.groupBrief != null) {
            group_DetailsField.fieldValue_TextView.text = vs.groupBrief.name
        }
        teacher_DetailsField.setVisible(vs.teacher != null)
        if (vs.teacher != null) {
            teacher_DetailsField.fieldValue_TextView.text = vs.teacher.fio
            teacher_DetailsField.fieldValue_TextView.setTextColor(resources.color(
                    if (vs.authedUserAccountType == AccountType.TEACHER && vs.authedUserDetailsId == vs.teacher.id)
                        R.color.colorAccent
                    else
                        android.R.color.black
            ))
        }

        adapter.uploadStatuses = vs.uploadingAttendingStateStatuses
        adapter.userCanChangeAttendingState = vs.isUserCanWriteGroup()

        val modelList = vs.attendings ?: emptyList()
        adapter.adapterList = BaseRecyclerViewAdapter.AdapterList(modelList, AllItemsLoaded(modelList.size).takeIf { modelList.isEmpty() })//todo

        paymentsMenuItem?.isVisible = currentViewState.isNavigateToPaymentsVisible()
    }

    override fun executeAction(va: ViewAction) {
        if (va !is LessonDetailsViewAction) return super.executeAction(va)
        when (va) {
            is LessonDetailsViewAction.NavigateToPayments -> DiHolder.router.navigateTo(Screen.PaymentsInGroup(va.groupId, va.monthNumber))
        }.also { }
    }
}