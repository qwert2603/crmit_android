package com.qwert2603.crmit_android.navigation

import android.support.v4.app.Fragment
import com.qwert2603.crmit_android.about.AboutFragment
import com.qwert2603.crmit_android.cabinet.CabinetFragment
import com.qwert2603.crmit_android.details_fragments.*
import com.qwert2603.crmit_android.greeting.GreetingFragment
import com.qwert2603.crmit_android.lesson_details.LessonDetailsFragmentBuilder
import com.qwert2603.crmit_android.list_fragments.*
import com.qwert2603.crmit_android.login.LoginFragment
import com.qwert2603.crmit_android.payments_in_group.PaymentsInGroupFragmentBuilder
import java.io.Serializable

sealed class Screen(
        private val fragmentCreator: () -> Fragment = { null!! },
        val allowDrawer: Boolean = true
) : ru.terrakok.cicerone.android.support.SupportAppScreen(), Serializable {

    override fun getFragment(): Fragment = fragmentCreator().also { it.setScreen(this) }

    object Sections : Screen({ SectionsListFragment() })
    object Groups : Screen({ GroupsListFragment() })
    object Masters : Screen({ MastersListFragment() })
    object Teachers : Screen({ TeachersListFragment() })
    object Students : Screen({ StudentsListFragment() })
    object LastSeens : Screen({ LastSeensListFragment() })
    object AccessTokens : Screen({ AccessTokensListFragment() })

    data class SectionDetails(override val key: DetailsScreenKey) : Screen({
        SectionDetailsFragmentBuilder
                .newSectionDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class GroupDetails(override val key: DetailsScreenKey) : Screen({
        GroupDetailsFragmentBuilder
                .newGroupDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class MasterDetails(override val key: DetailsScreenKey) : Screen({
        MasterDetailsFragmentBuilder
                .newMasterDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class TeacherDetails(override val key: DetailsScreenKey) : Screen({
        TeacherDetailsFragmentBuilder
                .newTeacherDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class StudentDetails(override val key: DetailsScreenKey) : Screen({
        StudentDetailsFragmentBuilder
                .newStudentDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class StudentsInGroup(val groupId: Long, val groupName: String) : Screen({
        StudentsInGroupListFragmentBuilder.newStudentsInGroupListFragment(groupId, groupName)
    })

    data class PaymentsInGroup(val groupId: Long, val monthNumber: Int? = null) : Screen({
        PaymentsInGroupFragmentBuilder(groupId)
                .also { if (monthNumber != null) it.monthNumber(monthNumber) }
                .build()
    })

    data class LessonsInGroup(val groupId: Long, val groupName: String) : Screen({
        LessonsInGroupListFragmentBuilder.newLessonsInGroupListFragment(groupId, groupName)
    })

    data class LessonDetails(val lessonId: Long) : Screen({ LessonDetailsFragmentBuilder.newLessonDetailsFragment(lessonId) })

    object About : Screen({ AboutFragment() })
    object Greeting : Screen({ GreetingFragment() }, allowDrawer = false)
    object Login : Screen({ LoginFragment() }, allowDrawer = false)
    object Cabinet : Screen({ CabinetFragment() })
}