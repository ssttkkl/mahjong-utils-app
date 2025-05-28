package io.ssttkkl.mahjongutils.app.components.appscaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import platform.UIKit.UIViewController

@Composable
actual fun rememberAppStateExtra(): Map<String, Any?> {
    val vc = LocalUIViewController.current
    return remember(vc) {
        mapOf("UIViewController" to vc)
    }
}

val AppState.uiViewController: UIViewController
    get() = extra["UIViewController"] as UIViewController