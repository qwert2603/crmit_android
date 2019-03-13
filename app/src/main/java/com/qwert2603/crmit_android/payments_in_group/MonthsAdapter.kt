package com.qwert2603.crmit_android.payments_in_group

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.GroupFull
import com.qwert2603.crmit_android.payments.PaymentsFragmentBuilder
import com.qwert2603.crmit_android.util.CrmitConst

class MonthsAdapter(
        childFragmentManager: FragmentManager,
        private val resources: Resources,
        private val groupFull: GroupFull
) : FragmentStatePagerAdapter(childFragmentManager) {

    companion object {
        private fun Int.toMonthTabTitle(resources: Resources): String {
            val monthName = resources.getStringArray(R.array.month_names)[this % CrmitConst.MONTHS_PER_YEAR].take(3)
            return "${this / CrmitConst.MONTHS_PER_YEAR + CrmitConst.START_YEAR}\n$monthName"
        }
    }

    override fun getItem(position: Int): Fragment = PaymentsFragmentBuilder.newPaymentsFragment(
            groupFull.id,
            groupFull.startMonth + position
    )

    override fun getCount() = groupFull.monthsCount()

    override fun getPageTitle(position: Int) = (groupFull.startMonth + position).toMonthTabTitle(resources)
}