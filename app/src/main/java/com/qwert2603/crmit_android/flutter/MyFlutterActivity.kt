package com.qwert2603.crmit_android.flutter

import android.os.Bundle
import com.qwert2603.andrlib.util.LogUtils

import com.qwert2603.crmit_android.di.DiHolder
import com.qwert2603.crmit_android.entity.AccountType
import com.qwert2603.crmit_android.env.E
import com.qwert2603.crmit_android.navigation.DetailsScreenKey
import com.qwert2603.crmit_android.navigation.Screen

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

/**
 * To add "crmit_schedule" to this "crmit_android" project create
 * "New Flutter Project" -> "Flutter Module" with name "crmit_schedule_module"
 * and copy sources from "https://github.com/qwert2603/crmit_schedule" to that new project.
 * That project is linked to crmit_android in "settings.gradle".
 *
 * When adding Flutter module to existing Android app with flavors
 * it's needed to specify same flavors in build.gradle(flutter):
 *
 * flavorDimensions 'server'
 * productFlavors {
 *     home { dimension "server" }
 *     prod { dimension "server" }
 * }
 */
class MyFlutterActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GeneratedPluginRegistrant.registerWith(this)

        val methodChannel = MethodChannel(flutterView, "app.channel.schedule")

        methodChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                "getAuthedTeacherIdOrZero" -> {
                    val id = DiHolder.userSettingsRepo
                            .loginResult.field.t
                            ?.let { loginResult ->
                                loginResult.detailsId
                                        .takeIf { loginResult.accountType == AccountType.TEACHER }
                            }
                            ?: 0
                    result.success(id)
                }
                "getAccessToken" -> result.success(DiHolder.userSettingsRepo.getAccessTokenSafe())
                "getBaseUrl" -> result.success(E.env.restBaseUrl2)
                "on401" -> {
                    DiHolder.on401()
                    this@MyFlutterActivity.finish()
                    result.success(null)
                }
                "getCacheDir" -> result.success(DiHolder.flutterCacheDir.path)
                "navigateToGroup" -> {
                    val groupId: Long = call.argument("groupId")!!
                    val groupName: String = call.argument("groupName")!!
                    LogUtils.d("MyFlutterActivity navigateToGroup $groupId $groupName")
                    this@MyFlutterActivity.finish()
                    DiHolder.router.navigateTo(Screen.GroupDetails(DetailsScreenKey(
                            entityId = groupId,
                            entityName = groupName
                    )))
                    result.success(null)
                }
                "onNavigationIconClicked" -> {
                    this@MyFlutterActivity.finish()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }
}
