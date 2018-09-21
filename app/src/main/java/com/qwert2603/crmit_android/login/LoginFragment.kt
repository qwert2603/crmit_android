package com.qwert2603.crmit_android.login

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qwert2603.andrlib.util.inflate
import com.qwert2603.crmit_android.R
import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.navigation.ScreenKey
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            container?.inflate(R.layout.fragment_login)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enter_Button.setOnClickListener { DiHolder.router.newRootScreen(ScreenKey.MASTERS.name) }
        super.onViewCreated(view, savedInstanceState)
    }
}