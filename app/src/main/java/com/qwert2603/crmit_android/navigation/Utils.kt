package com.qwert2603.crmit_android.navigation

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.v4.app.Fragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder

private const val SCREEN_KEY_KEY = "SCREEN_KEY_KEY"

fun Fragment.setScreen(screen: Screen) {
    val args: Bundle = arguments ?: Bundle()
    args.putSerializable(SCREEN_KEY_KEY, screen)
    arguments = args
}

fun Fragment.getScreen(): Screen? = arguments?.getSerializable(SCREEN_KEY_KEY) as? Screen

fun NavigatorHolder.createLifecycleObserver(navigator: Navigator) = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        setNavigator(navigator)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        removeNavigator()
    }
}