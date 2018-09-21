package com.qwert2603.crmit_android.greeting

data class GreetingViewState(
        val messages: List<String>,
        val currentMessageIndex: Int
) {
    fun showBack() = currentMessageIndex > 0
    fun showForward() = currentMessageIndex < messages.lastIndex
    fun showStart() = currentMessageIndex == messages.lastIndex
}