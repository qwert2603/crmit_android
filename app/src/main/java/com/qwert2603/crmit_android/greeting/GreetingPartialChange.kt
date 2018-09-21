package com.qwert2603.crmit_android.greeting

import com.qwert2603.andrlib.base.mvi.PartialChange

sealed class GreetingPartialChange : PartialChange {
    data class CurrentIndexChanged(val index: Int) : GreetingPartialChange()
    object MoveBack : GreetingPartialChange()
    object MoveForward : GreetingPartialChange()
}