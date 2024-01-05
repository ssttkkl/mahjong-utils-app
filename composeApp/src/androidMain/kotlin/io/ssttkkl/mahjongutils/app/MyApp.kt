package io.ssttkkl.mahjongutils.app

import android.app.Application
import io.ssttkkl.mahjongutils.app.init.AppInit
import io.ssttkkl.mahjongutils.app.utils.FileUtils
import io.ssttkkl.mahjongutils.app.utils.log.AndroidLoggerFactory
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import okio.Path.Companion.toOkioPath

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