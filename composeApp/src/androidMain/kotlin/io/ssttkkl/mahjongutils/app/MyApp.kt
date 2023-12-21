package io.ssttkkl.mahjongutils.app

import android.app.Application
import io.ssttkkl.mahjongutils.app.utils.os.FileUtils
import okio.Path.Companion.toOkioPath

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FileUtils.setSandboxPath(applicationContext.filesDir!!.toOkioPath())
    }
}