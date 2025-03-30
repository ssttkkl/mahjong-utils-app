@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ssttkkl.mahjongutils.app.App
import io.ssttkkl.mahjongutils.app.getAppTypography
import io.ssttkkl.mahjongutils.app.init.AppInit
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.app_name
import mahjongutils.composeapp.generated.resources.icon_app
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Button
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Label

fun main() = application {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        Dialog(Frame(), e.message ?: "Error").apply {
            layout = FlowLayout()
            val label = Label(e.message)
            add(label)
            val button = Button("OK").apply {
                addActionListener { dispose() }
            }
            add(button)
            setSize(300,300)
            isVisible = true
        }
    }

    AppInit.doInit()
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.icon_app)
    ) {
        App(typography = getAppTypography())
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App(typography = getAppTypography())
}