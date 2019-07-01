package com.qwert2603.crmit_android.about

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.andrlib.util.setVisible
import com.qwert2603.crmit_android.BuildConfig
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.navigation.MainActivity
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
                SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(BuildConfig.BIULD_TIME)),
                SimpleDateFormat("H:mm", Locale.getDefault()).format(Date(BuildConfig.BIULD_TIME))
        ))

        clearDB_Button.setVisible(E.env.showClearCacheButton)
        clearAll_Button.setVisible(E.env.showClearCacheButton)
        clearDB_Button.setOnClickListener { _ ->
            DiHolder.modelSchedulersProvider.io.scheduleDirect {
                DiHolder.clearDB()
            }
        }
        clearAll_Button.setOnClickListener { _ ->
            DiHolder.modelSchedulersProvider.io.scheduleDirect {
                DiHolder.clearAllData()
                DiHolder.userSettingsRepo.clearAll()

                DiHolder.uiSchedulerProvider.ui.scheduleDirect {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    System.exit(0)
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
}