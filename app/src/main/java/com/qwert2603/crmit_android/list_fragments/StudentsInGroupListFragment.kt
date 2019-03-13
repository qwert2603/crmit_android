package com.qwert2603.crmit_android.list_fragments

import android.os.Bundle
import android.view.View
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.andrlib.util.color
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.StudentInGroup
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen
import com.qwert2603.crmit_android.util.setStrike
import com.qwert2603.crmit_android.util.toMonthString
import com.qwert2603.crmit_android.util.toPointedString
import kotlinx.android.synthetic.main.item_student_in_group.view.*
import kotlinx.android.synthetic.main.toolbar_default.*

@FragmentWithArgs
class StudentsInGroupListFragment : EntitiesListFragment<StudentInGroup>() {

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    @Arg
    lateinit var groupName: String

    @Arg
    var groupStartMonth = -1

    @Arg
    var groupEndMonth = -1

    override val source = { _: Int, _: Int, _: String -> DiHolder.rest.getStudentsInGroup(groupId) }

    override val dbDaoInterface by lazy { DiHolder.studentInGroupDao.wrap(groupId) }

    override val vhLayoutRes = R.layout.item_student_in_group

    override val entityPluralsRes = R.plurals.students

    override val pageSize = Int.MAX_VALUE

    override fun View.bindEntity(e: StudentInGroup) {
        fio_TextView.text = e.studentFio
        fio_TextView.setStrike(!e.systemUserEnabled)
        fio_TextView.transitionName = "entity_name_${e.studentId}"

        enterMonth_TextView.setTextColor(resources.color(if (e.enterMonth == groupStartMonth) android.R.color.black else R.color.colorAccent))
        enterMonth_TextView.text = e.enterMonth.toMonthString(resources)

        exitMonth_TextView.setTextColor(resources.color(if (e.exitMonth == groupEndMonth) android.R.color.black else R.color.colorAccent))
        exitMonth_TextView.text = e.exitMonth.toMonthString(resources)

        discount_TextView.setVisible(e.discount > 0)
        if (e.discount > 0) {
            discount_TextView.text = getString(R.string.discount_format, e.discount.toPointedString())
        }

        lessonsAttendedCount_TextView.text = resources.getQuantityString(R.plurals.lessons_attended, e.lessonsAttendedCount, e.lessonsAttendedCount)
    }

    override var withSearch = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.title = getString(R.string.title_students_in_group, groupName)

        adapter.modelItemClicks
                .subscribe {
                    DiHolder.router.navigateTo(Screen.StudentDetails(DetailsScreenKey(
                            entityId = it.studentId,
                            entityName = it.studentFio,
                            entityNameTextView = _list_RecyclerView.findViewHolderForItemId(it.id).itemView.fio_TextView,
                            entityNameStrike = !it.systemUserEnabled
                    )))
                }
                .disposeOnDestroyView()

        super.onViewCreated(view, savedInstanceState)
    }
}