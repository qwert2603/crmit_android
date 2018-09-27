package com.qwert2603.crmit_android.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewGroupCompat
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.fragmentargs.FragmentArgs
import com.qwert2603.andrlib.base.mvi.BaseFragment
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.about.AboutFragment
import com.qwert2603.crmit_android.cabinet.CabinetFragment
import com.qwert2603.crmit_android.details_fragments.*
import com.qwert2603.crmit_android.entity_details.EntityDetailsFragment
import com.qwert2603.crmit_android.greeting.GreetingFragment
import com.qwert2603.crmit_android.lesson_details.LessonDetailsFragmentBuilder
import com.qwert2603.crmit_android.list_fragments.*
import com.qwert2603.crmit_android.login.LoginFragment
import com.qwert2603.crmit_android.payments_in_group.PaymentsInGroupFragmentBuilder
import com.squareup.leakcanary.LeakCanary
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class Navigator(private val activity: ActivityInterface)
    : SupportFragmentNavigator(activity.supportFragmentManager, activity.fragmentContainer) {

    init {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentPreCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
                        FragmentArgs.inject(f)
                    }

                    override fun onFragmentCreated(fm: FragmentManager?, f: Fragment, savedInstanceState: Bundle?) {
                        LeakCanary.installedRefWatcher().watch(f)
                    }

                    override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
                        (v as? ViewGroup)?.let { ViewGroupCompat.setTransitionGroup(it, true) }
                    }
                },
                true
        )

        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentResumed(fm: FragmentManager?, f: Fragment) {
                        activity.navigationActivity.onFragmentResumed(f)
                    }

                    override fun onFragmentPaused(fm: FragmentManager?, f: Fragment) {
                        activity.navigationActivity.onFragmentPaused(f)
                    }

                    @SuppressLint("RtlHardcoded")
                    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                        f.exitTransition = Slide(Gravity.LEFT)
                                .also { it.duration = TRANSITION_DURATION }
                        f.enterTransition = Slide(if (fm.backStackEntryCount > 0 || f is LoginFragment) Gravity.RIGHT else Gravity.LEFT)
                                .also { it.duration = TRANSITION_DURATION }
                        val sharedElementTransition = TransitionInflater.from(f.requireContext())
                                .inflateTransition(R.transition.shared_element)
                        f.sharedElementEnterTransition = sharedElementTransition
                        f.sharedElementReturnTransition = sharedElementTransition
                    }
                },
                false
        )
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun createFragment(screenKey: String, data: Any?) = when (ScreenKey.valueOf(screenKey)) {
        ScreenKey.SECTIONS -> SectionsListFragment()
        ScreenKey.GROUPS -> GroupsListFragment()
        ScreenKey.TEACHERS -> TeachersListFragment()
        ScreenKey.MASTERS -> MastersListFragment()
        ScreenKey.STUDENTS -> StudentsListFragment()
        ScreenKey.ABOUT -> AboutFragment()
        ScreenKey.STUDENT_DETAILS -> (data as EntityDetailsFragment.Key).let { StudentDetailsFragmentBuilder.newStudentDetailsFragment(it.entityId, it.entityName, it.entityNameColorAccent, it.entityNameStrike) }
        ScreenKey.SECTION_DETAILS -> (data as EntityDetailsFragment.Key).let { SectionDetailsFragmentBuilder.newSectionDetailsFragment(it.entityId, it.entityName, it.entityNameColorAccent, it.entityNameStrike) }
        ScreenKey.GROUP_DETAILS -> (data as EntityDetailsFragment.Key).let { GroupDetailsFragmentBuilder.newGroupDetailsFragment(it.entityId, it.entityName, it.entityNameColorAccent, it.entityNameStrike) }
        ScreenKey.TEACHER_DETAILS -> (data as EntityDetailsFragment.Key).let { TeacherDetailsFragmentBuilder.newTeacherDetailsFragment(it.entityId, it.entityName, it.entityNameColorAccent, it.entityNameStrike) }
        ScreenKey.MASTER_DETAILS -> (data as EntityDetailsFragment.Key).let { MasterDetailsFragmentBuilder.newMasterDetailsFragment(it.entityId, it.entityName, it.entityNameColorAccent, it.entityNameStrike) }
        ScreenKey.STUDENTS_IN_GROUP -> (data as StudentsInGroupListFragment.Key).let { StudentsInGroupListFragmentBuilder.newStudentsInGroupListFragment(it.groupId, it.groupName) }
        ScreenKey.LESSONS_IN_GROUP -> (data as LessonsInGroupListFragment.Key).let { LessonsInGroupListFragmentBuilder.newLessonsInGroupListFragment(it.groupId, it.groupName) }
        ScreenKey.LESSON_DETAILS -> LessonDetailsFragmentBuilder.newLessonDetailsFragment(data as Long)
        ScreenKey.PAYMENTS_IN_GROUP -> PaymentsInGroupFragmentBuilder.newPaymentsInGroupFragment(data as Long)
        ScreenKey.GREETING -> GreetingFragment()
        ScreenKey.LOGIN -> LoginFragment()
        ScreenKey.CABINET -> CabinetFragment()
    }.also { it.setScreenKey(ScreenKey.valueOf(screenKey)) }

    override fun exit() {
        activity.finish()
    }

    override fun showSystemMessage(message: String) {
        /** make delay to wait for new fragment to show snackbar on its [BaseFragment.viewForSnackbar] */
        activity.viewForSnackbars().postDelayed({
            Snackbar.make(activity.viewForSnackbars(), message, Snackbar.LENGTH_SHORT).show()
        }, 380)
    }

    @SuppressLint("RtlHardcoded")
    override fun setupFragmentTransactionAnimation(command: Command, currentFragment: Fragment?, nextFragment: Fragment, fragmentTransaction: FragmentTransaction) {
        currentFragment?.exitTransition = Slide(Gravity.LEFT)
                .also { it.duration = TRANSITION_DURATION }
        nextFragment.enterTransition = Slide(if (command is Forward || nextFragment is LoginFragment) Gravity.RIGHT else Gravity.LEFT)
                .also { it.duration = TRANSITION_DURATION }

        command
                .let { it as? Forward }
                ?.transitionData
                .let { it as? EntityDetailsFragment.Key }
                ?.entityNameTextView
                ?.also { fragmentTransaction.addSharedElement(it, it.transitionName) }

        if (command is Replace) {
            currentFragment?.sharedElementReturnTransition = null
        }
    }

    override fun applyCommand(command: Command?) {
        activity.hideKeyboard()
        super.applyCommand(command)
    }

    companion object {
        private const val TRANSITION_DURATION = 300L
    }
}