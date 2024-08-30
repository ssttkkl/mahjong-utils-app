package io.ssttkkl.mahjongutils.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.ssttkkl.mahjongutils.app.init.AppInit
import io.ssttkkl.mahjongutils.app.utils.ActivityHelper

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        _current = this

        registerActivityLifecycleCallbacks(object :ActivityLifecycleCallbacks {
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

        AppInit.doInit()
    }

    companion object {
        private var _current: MyApp? = null
        val current: MyApp
            get() = checkNotNull(_current)
    }
}