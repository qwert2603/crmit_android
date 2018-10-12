package com.qwert2603.crmit_android.cabinet

import com.qwert2603.andrlib.base.mvi.load_refresh.LRModel
import com.qwert2603.andrlib.base.mvi.load_refresh.LRViewState
import com.qwert2603.andrlib.generator.GenerateLRChanger
import com.qwert2603.crmit_android.entity.Lesson
import com.qwert2603.crmit_android.entity.LoginResult

@GenerateLRChanger
data class CabinetViewState(
        override val lrModel: LRModel,
        val loginResult: LoginResult?,
        val fio: String?,
        val lastLessons: List<Lesson>?,
        val isLogout: Boolean
) : LRViewState