package com.qwert2603.crmit_android.list_fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.CrmitApplication
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entities_list.EntitiesListViewState
import com.qwert2603.crmit_android.entity.Lesson
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.rest.Rest
import com.qwert2603.crmit_android.util.toShowingDate
import kotlinx.android.synthetic.main.item_lesson.view.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.SimpleDateFormat
import java.util.*

@FragmentWithArgs
class LessonsInGroupListFragment : EntitiesListFragment<Lesson>() {

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var groupName: String

    override val source = { _: Int, _: Int, _: String -> DiHolder.rest.getLessonsInGroup(groupId) }

    override val dbDaoInterface by lazy { DiHolder.lessonDao.wrap(groupId) }

    override val vhLayoutRes = R.layout.item_lesson

    override val entityPluralsRes = R.plurals.lessons

    override val pageSize = Int.MAX_VALUE

    private lateinit var today: String

    private val fontTypeface by lazy { ResourcesCompat.getFont(requireContext(), CrmitApplication.appFontRes) }

    override fun View.bindEntity(e: Lesson) {
        date_TextView.text = e.date.toShowingDate()
        date_TextView.setTypeface(fontTypeface, if (e.date <= today) Typeface.BOLD else Typeface.NORMAL)
        date_TextView.setTextColor(resources.color(if (e.date == today) R.color.colorAccent else android.R.color.black))
        teacherFio_TextView.setVisible(e.anotherTeacherFio != null)
        if (e.anotherTeacherFio != null) teacherFio_TextView.text = e.anotherTeacherFio
    }

    override var withSearch = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.title = getString(R.string.title_lessons_in_group, groupName)

        adapter.modelItemClicks
                .subscribe { DiHolder.router.navigateTo(Screen.LessonsInGroup(it.groupId, it.date)) }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun render(vs: EntitiesListViewState<Lesson>) {
        today = SimpleDateFormat(Rest.DATE_FORMAT, Locale.getDefault()).format(Date())
        super.render(vs)
    }
}