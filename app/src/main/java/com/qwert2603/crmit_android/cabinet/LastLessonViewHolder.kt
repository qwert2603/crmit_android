package com.qwert2603.crmit_android.cabinet

import android.view.ViewGroup
import com.qwert2603.andrlib.base.recyclerview.BaseRecyclerViewHolder
import com.qwert2603.andrlib.util.color
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.entity.Lesson
import com.qwert2603.crmit_android.rest.Rest
import com.qwert2603.crmit_android.util.toShowingDate
import kotlinx.android.synthetic.main.item_last_lesson.view.*
import java.text.SimpleDateFormat
import java.util.*

class LastLessonViewHolder(parent: ViewGroup) : BaseRecyclerViewHolder<Lesson>(parent, R.layout.item_last_lesson) {
    override fun bind(m: Lesson) = with(itemView) {
        super.bind(m)

        date_TextView.text = m.date.toShowingDate()
        val today = SimpleDateFormat(Rest.DATE_FORMAT, Locale.getDefault()).format(Date())
        date_TextView.setTextColor(resources.color(if (m.date == today) R.color.colorAccent else android.R.color.black))
        groupName_TextView.text = m.groupName
    }
}