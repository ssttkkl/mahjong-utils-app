package io.ssttkkl.mahjongutils.app.kuikly.screens.about

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.core.annotations.Page
import io.ssttkkl.mahjongutils.app.kuikly.BuildKonfig
import io.ssttkkl.mahjongutils.app.kuikly.components.Screen
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.ListItem

internal object AboutScreen : Screen() {
    override val path: String
        get() = "about"

    override val title: String
        @Composable
        get() = "关于"

    @Composable
    override fun ScreenContent() {
        ScreenScaffold { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                ListItem(
                    headlineContent = { Text("应用版本") },
                    supportingContent = { Text(BuildKonfig.VERSION_NAME) }
                )
                ListItem(
                    headlineContent = { Text("开源仓库") },
                    supportingContent = { Text(BuildKonfig.OPENSOURCE_REPO) },
                    modifier = Modifier.clickable {
//                    uriHandler.openUri(BuildKonfig.OPENSOURCE_REPO)
                    }
                )
                ListItem(
                    headlineContent = { Text("开源协议") },
                    supportingContent = { Text(BuildKonfig.OPENSOURCE_LICENSE) },
                    modifier = Modifier.clickable {
//                    navigator?.push(OpenSourceLicensesScreen)
                    }
                )
            }
        }
    }
}

@Page("about")
internal class AboutPager : ComposeContainer() {
    override fun willInit() {
        super.willInit()

        val screen = AboutScreen
        setContent {
            screen.ScreenContent()
        }
    }
}