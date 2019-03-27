package com.qwert2603.crmit_android.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewGroupCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.hannesdorfmann.fragmentargs.FragmentArgs
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.login.LoginFragment
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.*

class Navigator(private val activity: ActivityInterface)
    : SupportAppNavigator(activity.fragmentActivity, activity.supportFragmentManager, activity.fragmentContainer) {

    init {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                        FragmentArgs.inject(f)
                    }

                    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                        (v as? ViewGroup)?.let { ViewGroupCompat.setTransitionGroup(it, true) }
                    }
                },
                true
        )

        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                        activity.navigationActivity.onFragmentResumed(f)
                    }

                    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
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

    @SuppressLint("RtlHardcoded")
    override fun setupFragmentTransaction(command: Command, currentFragment: Fragment?, nextFragment: Fragment, fragmentTransaction: FragmentTransaction) {
        currentFragment
                ?.exitTransition = Slide(Gravity.LEFT)
                .also { it.duration = TRANSITION_DURATION }
        nextFragment
                .enterTransition = Slide(if (command is Forward || nextFragment is LoginFragment) Gravity.RIGHT else Gravity.LEFT)
                .also { it.duration = TRANSITION_DURATION }

        command
                .let { it as? Forward }
                ?.screen
                .let { it as? DetailsScreen }
                ?.key
                ?.entityNameTextView
                ?.also { fragmentTransaction.addSharedElement(it, it.transitionName) }

        if (command is Replace) {
            currentFragment?.sharedElementReturnTransition = null
        }
    }

    override fun applyCommand(command: Command?) {
        FirebaseAnalytics.getInstance(activity.fragmentActivity).logEvent(
                "navigation",
                Bundle()
                        .also { it.putString("command", command?.javaClass?.simpleName.toString()) }
                        .also { it.putString("screen", command?.getScreen()?.javaClass?.simpleName.toString()) }
        )
        activity.hideKeyboard()
        super.applyCommand(command)
    }

    companion object {
        private const val TRANSITION_DURATION = 300L

        private fun Command.getScreen(): Screen? = when (this) {
            is Forward -> this.screen as? Screen
            is Replace -> this.screen as? Screen
            is BackTo -> this.screen as? Screen
            is Back -> null
            else -> null
        }
    }
}