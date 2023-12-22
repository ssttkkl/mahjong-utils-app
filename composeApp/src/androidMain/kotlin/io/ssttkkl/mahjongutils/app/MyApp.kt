package io.ssttkkl.mahjongutils.app

import android.app.Application
import io.ssttkkl.mahjongutils.app.utils.FileUtils
import okio.Path.Companion.toOkioPath

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MyApp._current = this

        FileUtils.setSandboxPath(applicationContext.filesDir!!.toOkioPath())
    }

    companion object {
        private var _current: MyApp? = null
        val current: MyApp
            get() = checkNotNull(_current)
    }
}