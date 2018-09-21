package com.qwert2603.crmit_android.greeting

import com.qwert2603.andrlib.base.mvi.BasePresenter
import com.qwert2603.andrlib.base.mvi.PartialChange
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import io.reactivex.Observable

class GreetingPresenter : BasePresenter<GreetingView, GreetingViewState>(DiHolder.uiSchedulerProvider) {

    override val initialState = GreetingViewState(
            messages = DiHolder.resources.getStringArray(R.array.greeting).toList(),
            currentMessageIndex = 0
    )

    override val partialChanges: Observable<PartialChange> = Observable.merge(
            intent { it.currentIndexChanges() }.map { GreetingPartialChange.CurrentIndexChanged(it) },
            intent { it.backClicks() }.map { GreetingPartialChange.MoveBack },
            intent { it.forwardClicks() }.map { GreetingPartialChange.MoveForward }
    )

    override fun stateReducer(vs: GreetingViewState, change: PartialChange): GreetingViewState {
        if (change !is GreetingPartialChange) throw Exception()
        return when (change) {
            is GreetingPartialChange.CurrentIndexChanged -> vs.copy(currentMessageIndex = change.index)
            GreetingPartialChange.MoveBack -> vs.copy(currentMessageIndex = maxOf(vs.currentMessageIndex - 1, 0))
            GreetingPartialChange.MoveForward -> vs.copy(currentMessageIndex = minOf(vs.currentMessageIndex + 1, vs.messages.lastIndex))
        }
    }

    override fun bindIntents() {
        super.bindIntents()

        intent { it.startClicks() }
                .doOnNext {
                    DiHolder.userSettingsRepo.greetingShown = true
                    viewActions.onNext(GreetingViewAction.MoveToLogin)
                }
                .subscribeToView()
    }
}