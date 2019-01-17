package com.qwert2603.crmit_android.lessons_in_group

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.base.mvi.load_refresh.LRFragment
import com.qwert2603.andrlib.base.mvi.load_refresh.LoadRefreshPanel
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.renderIfChanged
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.lesson_details.LessonDetailsFragmentBuilder
import com.qwert2603.crmit_android.rest.Rest
import com.qwert2603.crmit_android.util.SaveImageLifecycleObserver
import com.qwert2603.crmit_android.util.mapNotNull
import com.qwert2603.crmit_android.util.toShowingDate
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_lessons_in_group.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.SimpleDateFormat
import java.util.*

@FragmentWithArgs
class LessonsInGroupFragment : LRFragment<LessonsInGroupViewState, LessonsInGroupView, LessonsInGroupPresenter>(), LessonsInGroupView, ParentLessonsFragment {

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var date: String

    override fun createPresenter() = LessonsInGroupPresenter(groupId, date)

    override fun loadRefreshPanel(): LoadRefreshPanel = lrPanelImpl

    private val onRetryDateClicked = PublishSubject.create<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(SaveImageLifecycleObserver())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_lessons_in_group)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_lesson_in_group_default)
        lessons_ViewPager.offscreenPageLimit = 2

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onRetryDateClicked() {
        onRetryDateClicked.onNext(Any())
    }

    override fun retry(): Observable<Any> = Observable.merge(
            super.retry(),
            onRetryDateClicked
    )

    override fun dateSelected(): Observable<String> = RxViewPager.pageSelections(lessons_ViewPager)
            .skipInitialValue()
            .mapNotNull { currentViewState.lessons?.get(it)?.date }

    override fun render(vs: LessonsInGroupViewState) {
        super.render(vs)

        renderIfChanged({ lessons }) { lessons ->
            if (lessons != null) {
                lessons_ViewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
                    override fun getItem(index: Int): Fragment = LessonDetailsFragmentBuilder.newLessonDetailsFragment(lessons[index].id)
                    override fun getCount() = lessons.size
                }
                lessons_ViewPager.currentItem = vs.selectedIndex() ?: 0
            } else {
                lessons_ViewPager.adapter = null
            }

            lessons_ViewPager.setVisible(lessons != null)
        }
        renderIfChanged({ selectedIndex() }) { selectedIndex ->
            lessons_ViewPager.setCurrentItem(selectedIndex ?: 0, false)
            if (selectedIndex != null && vs.lessons != null) {
                toolbar.title = "${vs.lessons[selectedIndex].date.toShowingDate()} (${selectedIndex + 1}/${vs.lessons.size})"
                val today = SimpleDateFormat(Rest.DATE_FORMAT, Locale.getDefault()).format(Date())
                toolbar.setTitleTextColor((resources.color(if (vs.lessons[selectedIndex].date == today) R.color.colorAccent else android.R.color.black)))
            }
        }
    }

    override fun executeAction(va: ViewAction) {
        if (va !is LessonsInGroupViewAction) return super.executeAction(va)
        when (va) {
            LessonsInGroupViewAction.ShowingCachedData -> Snackbar.make(coordinatorLayout, R.string.text_showing_cached_data, Snackbar.LENGTH_SHORT).show()
            LessonsInGroupViewAction.ShowThereWillBeAttendingChangesCaching -> ThereWillBeAttendingChangesCachingDialogFragment().show(fragmentManager, null)
        }.also { }
    }
}