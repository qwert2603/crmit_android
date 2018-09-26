package com.qwert2603.crmit_android.payments_in_group

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.payments.PaymentsFragmentBuilder

class MonthsAdapter(
        childFragmentManager: FragmentManager,
        private val resources: Resources,
        private val groupFull: GroupFull
) : FragmentStatePagerAdapter(childFragmentManager) {

    companion object {
        private fun Int.toMonthTabTitle(resources: Resources) = "${this / 12 + 2017}\n${resources.getStringArray(R.array.month_names)[this % 12].take(3)}"
    }

    override fun getItem(position: Int): Fragment = PaymentsFragmentBuilder.newPaymentsFragment(
            groupFull.id,
            groupFull.startMonth + position
    )

    override fun getCount() = groupFull.monthsCount()

    override fun getPageTitle(position: Int) = (groupFull.startMonth + position).toMonthTabTitle(resources)
}