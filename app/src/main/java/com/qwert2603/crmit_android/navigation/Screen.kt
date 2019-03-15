package com.qwert2603.crmit_android.navigation

import androidx.fragment.app.Fragment
import com.qwert2603.crmit_android.about.AboutFragment
import com.qwert2603.crmit_android.cabinet.CabinetFragment
import com.qwert2603.crmit_android.details_fragments.*
import com.qwert2603.crmit_android.greeting.GreetingFragment
import com.qwert2603.crmit_android.lesson_details.LessonDetailsFragmentBuilder
import com.qwert2603.crmit_android.lessons_in_group.LessonsInGroupFragmentBuilder
import com.qwert2603.crmit_android.list_fragments.*
import com.qwert2603.crmit_android.login.LoginFragment
import com.qwert2603.crmit_android.payments_in_group.PaymentsInGroupFragmentBuilder
import ru.terrakok.cicerone.android.support.SupportAppScreen
import java.io.Serializable

// don't use objects because of serialization.
sealed class Screen(
        private val fragmentCreator: () -> Fragment = { null!! },
        val allowDrawer: Boolean = true
) : SupportAppScreen(), Serializable {

    override fun getFragment(): Fragment = fragmentCreator().also { it.setScreen(this) }

    data class Sections(@Transient private val ignored: Unit? = null) : Screen({ SectionsListFragment() })
    data class Groups(@Transient private val ignored: Unit? = null) : Screen({ GroupsListFragment() })
    data class Masters(@Transient private val ignored: Unit? = null) : Screen({ MastersListFragment() })
    data class Developers(@Transient private val ignored: Unit? = null) : Screen({ DevelopersListFragment() })
    data class Bots(@Transient private val ignored: Unit? = null) : Screen({ BotsListFragment() })
    data class Teachers(@Transient private val ignored: Unit? = null) : Screen({ TeachersListFragment() })
    data class Students(@Transient private val ignored: Unit? = null) : Screen({ StudentsListFragment() })
    data class LastSeens(@Transient private val ignored: Unit? = null) : Screen({ LastSeensListFragment() })
    data class AccessTokens(@Transient private val ignored: Unit? = null) : Screen({ AccessTokensListFragment() })

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

    data class DeveloperDetails(override val key: DetailsScreenKey) : Screen({
        DeveloperDetailsFragmentBuilder
                .newDeveloperDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class BotDetails(override val key: DetailsScreenKey) : Screen({
        BotDetailsFragmentBuilder
                .newBotDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class TeacherDetails(override val key: DetailsScreenKey) : Screen({
        TeacherDetailsFragmentBuilder
                .newTeacherDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class StudentDetails(override val key: DetailsScreenKey) : Screen({
        StudentDetailsFragmentBuilder
                .newStudentDetailsFragment(key.entityId, key.entityName, key.entityNameColorAccent, key.entityNameStrike)
    }), DetailsScreen

    data class StudentsInGroup(
            val groupId: Long,
            val groupName: String,
            val groupStartMonth: Int,
            val groupEndMonth: Int
    ) : Screen({
        StudentsInGroupListFragmentBuilder.newStudentsInGroupListFragment(groupEndMonth, groupId, groupName, groupStartMonth)
    })

    data class PaymentsInGroup(val groupId: Long, val monthNumber: Int? = null) : Screen({
        PaymentsInGroupFragmentBuilder(groupId)
                .also { if (monthNumber != null) it.monthNumber(monthNumber) }
                .build()
    })

    data class LessonsInGroupList(val groupId: Long, val groupName: String) : Screen({
        LessonsInGroupListFragmentBuilder.newLessonsInGroupListFragment(groupId, groupName)
    })

    data class LessonsInGroup(val groupId: Long, val date: String) : Screen({
        LessonsInGroupFragmentBuilder.newLessonsInGroupFragment(date, groupId)
    })

    data class LessonDetails(val lessonId: Long, val asNested: Boolean) : Screen({
        LessonDetailsFragmentBuilder.newLessonDetailsFragment(asNested, lessonId)
    })

    data class About(@Transient private val ignored: Unit? = null) : Screen({ AboutFragment() })
    data class Greeting(@Transient private val ignored: Unit? = null) : Screen({ GreetingFragment() }, allowDrawer = false)
    data class Login(@Transient private val ignored: Unit? = null) : Screen({ LoginFragment() }, allowDrawer = false)
    data class Cabinet(@Transient private val ignored: Unit? = null) : Screen({ CabinetFragment() })
}