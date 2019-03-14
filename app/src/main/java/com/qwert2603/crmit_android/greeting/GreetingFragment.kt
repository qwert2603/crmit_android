package com.qwert2603.crmit_android.greeting

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager
import com.jakewharton.rxbinding2.view.RxView
import com.qwert2603.andrlib.base.mvi.BaseFragment
import com.qwert2603.andrlib.base.mvi.ViewAction
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.renderIfChanged
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.navigation.Screen
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_greeting.*
import kotlinx.android.synthetic.main.view_greeting_message.view.*

class GreetingFragment : BaseFragment<GreetingViewState, GreetingView, GreetingPresenter>(), GreetingView {

    override fun createPresenter() = GreetingPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_greeting)

    override fun currentIndexChanges(): Observable<Int> = RxViewPager.pageSelections(messages_ViewPager)

    override fun backClicks() = RxView.clicks(back_Button)

    override fun forwardClicks() = RxView.clicks(forward_Button)

    override fun startClicks() = RxView.clicks(go_Button)

    override fun render(vs: GreetingViewState) {
        super.render(vs)

        renderIfChanged({ messages }) { messages ->
            messages_ViewPager.adapter = object : PagerAdapter() {

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val view = container.inflate(R.layout.view_greeting_message)
                    @Suppress("DEPRECATION")
                    view.message_TextView.text = Html.fromHtml(messages[position])
                    container.addView(view)
                    return view
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(`object` as View)
                }

                override fun isViewFromObject(view: View, `object`: Any) = view === `object`

                override fun getCount() = messages.size
            }
        }
        renderIfChanged({ currentMessageIndex }) { messages_ViewPager.currentItem = it }

        pointsView.setProgress(vs.currentMessageIndex, vs.messages.size)

        back_Button.setVisible(vs.showBack())
        forward_Button.setVisible(vs.showForward())
        go_Button.setVisible(vs.showStart())
    }

    override fun executeAction(va: ViewAction) {
        if (va !is GreetingViewAction) return
        when (va) {
            GreetingViewAction.MoveToLogin -> DiHolder.router.newRootScreen(Screen.Login())
        }.also { }
    }
}