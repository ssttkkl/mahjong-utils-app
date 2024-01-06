package io.ssttkkl.mahjongutils.app

import android.app.Application
import io.ssttkkl.mahjongutils.app.init.AppInit

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        _current = this

        AppInit.doInit()
    }

    companion object {
        private var _current: MyApp? = null
        val current: MyApp
            get() = checkNotNull(_current)
    }
}