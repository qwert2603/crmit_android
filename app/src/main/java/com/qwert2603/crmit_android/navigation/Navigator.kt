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
import com.qwert2603.crmit_android.list_fragments.MastersListFragment
import com.qwert2603.crmit_android.list_fragments.SectionsListFragment
import com.qwert2603.crmit_android.list_fragments.StudentsListFragment
import com.qwert2603.crmit_android.list_fragments.TeachersListFragment
import com.qwert2603.crmit_android.student_details.StudentDetailsFragment
import com.qwert2603.crmit_android.student_details.StudentDetailsFragmentBuilder
import com.qwert2603.crmit_android.student_details.StudentDetailsKey
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

class Navigator(private val activity: ActivityInterface)
    : SupportFragmentNavigator(activity.supportFragmentManager, activity.fragmentContainer) {

    init {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentPreCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
                        FragmentArgs.inject(f)
                    }

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
                        f.enterTransition = Slide(if (fm.backStackEntryCount > 0) Gravity.RIGHT else Gravity.LEFT)
                                .also { it.duration = TRANSITION_DURATION }
                        val sharedElementTransition = TransitionInflater.from(f.requireContext())
                                .inflateTransition(R.transition.shared_element_student_fio)
                        f.sharedElementEnterTransition = sharedElementTransition
                        f.sharedElementReturnTransition = sharedElementTransition
                    }

                    override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
                        (v as? ViewGroup)?.let { ViewGroupCompat.setTransitionGroup(it, true) }
                    }
                }, false
        )
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun createFragment(screenKey: String, data: Any?) = when (ScreenKey.valueOf(screenKey)) {
        ScreenKey.SECTIONS -> SectionsListFragment()
        ScreenKey.TEACHERS -> TeachersListFragment()
        ScreenKey.MASTERS -> MastersListFragment()
        ScreenKey.STUDENTS -> StudentsListFragment()
        ScreenKey.STUDENT_DETAILS -> (data as StudentDetailsKey).let { StudentDetailsFragmentBuilder.newStudentDetailsFragment(it.studentFio, it.studentId, it.systemUserEnabled) }
        ScreenKey.ABOUT -> AboutFragment()
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
        nextFragment.enterTransition = Slide(if (command is Forward) Gravity.RIGHT else Gravity.LEFT)
                .also { it.duration = TRANSITION_DURATION }
        if (command is Forward && currentFragment is StudentsListFragment && nextFragment is StudentDetailsFragment) {
            val studentFioTextView = (command.transitionData as StudentDetailsKey).studentFioTextView
            fragmentTransaction.addSharedElement(studentFioTextView, studentFioTextView.transitionName)
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