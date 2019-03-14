package com.qwert2603.crmit_android.entity

data class Wrapper<T : Any>(val t: T?)

fun <T : Any> T?.wrap() = Wrapper(this)