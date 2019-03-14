package com.qwert2603.crmit_android.payments_in_group

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.GroupBrief
import com.qwert2603.crmit_android.payments.PaymentsFragmentBuilder
import com.qwert2603.crmit_android.util.CrmitConst

class MonthsAdapter(
        childFragmentManager: FragmentManager,
        private val resources: Resources,
        private val groupBrief: GroupBrief
) : FragmentStatePagerAdapter(childFragmentManager) {

    companion object {
        private fun Int.toMonthTabTitle(resources: Resources): String {
            val monthName = resources.getStringArray(R.array.month_names)[this % CrmitConst.MONTHS_PER_YEAR].take(3)
            return "${this / CrmitConst.MONTHS_PER_YEAR + CrmitConst.START_YEAR}\n$monthName".toUpperCase()
        }
    }

    override fun getItem(position: Int): Fragment = PaymentsFragmentBuilder.newPaymentsFragment(
            groupBrief.id,
            groupBrief.startMonth + position
    )

    override fun getCount() = groupBrief.monthsCount()

    override fun getPageTitle(position: Int) = (groupBrief.startMonth + position).toMonthTabTitle(resources)
}