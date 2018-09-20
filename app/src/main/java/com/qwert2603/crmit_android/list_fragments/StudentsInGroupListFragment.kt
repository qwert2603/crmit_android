package com.qwert2603.crmit_android.list_fragments

import android.view.View
import com.hannesdorfmann.fragmentargs.annotation.Arg
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs
import com.qwert2603.andrlib.model.IdentifiableLong
import com.qwert2603.crmit_android.db.DaoInterface
import com.qwert2603.crmit_android.db.generated_dao.wrap
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.StudentInGroup

@FragmentWithArgs
class StudentsInGroupListFragment : EntitiesListFragment<StudentInGroup>() {

    @Arg
    var groupId: Long = IdentifiableLong.NO_ID

    override val source = { _: Int, _: Int, _: String -> DiHolder.rest.getStudentsInGroup(groupId) }

    override val dbDaoInterface: DaoInterface<StudentInGroup> = DiHolder.studentInGroupDao.wrap(groupId)

    override val titleRes: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val vhLayoutRes: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun View.bindEntity(e: StudentInGroup) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val pageSize = Int.MAX_VALUE
}