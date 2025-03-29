package io.ssttkkl.mahjongutils.app.components.appscaffold

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAppStateExtra(): Map<String, Any?> {
    val activity = LocalActivity.current
    return remember(activity) {
        mapOf("Activity" to activity)
    }
}

val AppState.activity: Activity
    get() = extra["Activity"] as Activity