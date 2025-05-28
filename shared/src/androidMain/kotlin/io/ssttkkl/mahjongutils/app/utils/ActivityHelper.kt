package io.ssttkkl.mahjongutils.app.utils

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityHelper {
    private var currentActivityRef: WeakReference<Activity>? = null

    var currentActivity: Activity?
        get() = currentActivityRef?.get()
        set(value) {
            currentActivityRef = WeakReference(value)
        }
}