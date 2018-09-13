package com.qwert2603.crmit_android.about

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.BuildConfig
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.env.E
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.SimpleDateFormat
import java.util.*

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_about)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_about)

        @Suppress("DEPRECATION")
        appInfo_TextView.text = Html.fromHtml(getString(R.string.app_info_text_format,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE,
                SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(BuildConfig.BIULD_TIME)),
                SimpleDateFormat("H:mm", Locale.getDefault()).format(Date(BuildConfig.BIULD_TIME))
        ))

        clearCache_Button.setVisible(E.env.showClearCacheButton)
        clearCache_Button.setOnClickListener { _ ->
            DiHolder.modelSchedulersProvider.io.scheduleDirect {
                listOf(
                        DiHolder.masterDao,
                        DiHolder.teacherDao,
                        DiHolder.studentBriefDao,
                        DiHolder.studentFullDao,
                        DiHolder.sectionDao
                ).forEach { it.deleteAllItems() }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
}