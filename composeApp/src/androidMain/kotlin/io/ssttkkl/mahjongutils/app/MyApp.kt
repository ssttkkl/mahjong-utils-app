package io.ssttkkl.mahjongutils.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.ssttkkl.mahjongutils.app.base.utils.AppInstance
import io.ssttkkl.mahjongutils.app.base.utils.FileUtils
import io.ssttkkl.mahjongutils.app.utils.ActivityHelper
import okio.Path.Companion.toOkioPath

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInstance.app = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
                ActivityHelper.currentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
                if (ActivityHelper.currentActivity == activity) {
                    ActivityHelper.currentActivity = null
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        })

        FileUtils.setSandboxPath(filesDir!!.toOkioPath())
    }
}