package com.qwert2603.crmit_android.list_fragments

import android.view.View
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entities_list.EntitiesListFragment
import com.qwert2603.crmit_android.entity.Master
import kotlinx.android.synthetic.main.item_teacher.view.*

class MastersListFragment : EntitiesListFragment<Master>() {
    override val source = DiHolder.rest::getMastersList
    override val titleRes = R.string.title_masters
    override val vhLayoutRes = R.layout.item_master
    override val entityPluralsRes = R.plurals.masters
    override fun View.bindEntity(e: Master) {
        fio_TextView.text = e.fio
        login_TextView.text = e.systemUser.login
    }
}