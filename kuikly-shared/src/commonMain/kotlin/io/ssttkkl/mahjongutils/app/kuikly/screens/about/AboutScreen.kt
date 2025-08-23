package io.ssttkkl.mahjongutils.app.kuikly.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.core.annotations.Page
import io.ssttkkl.mahjongutils.app.kuikly.BuildKonfig
import io.ssttkkl.mahjongutils.app.kuikly.Res
import io.ssttkkl.mahjongutils.app.kuikly.components.LocalScreenState
import io.ssttkkl.mahjongutils.app.kuikly.components.Screen
import io.ssttkkl.mahjongutils.app.kuikly.compose.material3.ListItem
import io.ssttkkl.mahjongutils.app.kuikly.utils.Text
import io.ssttkkl.mahjongutils.app.kuikly.utils.stringResource
import kotlinx.coroutines.launch

internal object AboutScreen : Screen() {
    override val path: String
        get() = "about"

    override val title: String
        @Composable
        get() = stringResource(Res.strings.title_about)

    @Composable
    override fun ScreenContent() {
        ScreenScaffold { innerPadding ->
            val screenState = LocalScreenState.current
            val scope = rememberCoroutineScope()
            Column(Modifier.padding(innerPadding)) {
                ListItem(
                    headlineContent = { Text(Res.strings.title_about_appversion) },
                    supportingContent = { Text(BuildKonfig.VERSION_NAME) }
                )
                ListItem(
                    headlineContent = { Text(Res.strings.title_about_opensource_repo) },
                    supportingContent = { Text(BuildKonfig.OPENSOURCE_REPO) },
                    modifier = Modifier.clickable {
//                    uriHandler.openUri(BuildKonfig.OPENSOURCE_REPO)
                        scope.launch {
                            screenState.snackbarHostState
                                .showSnackbar(BuildKonfig.OPENSOURCE_REPO)
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text(Res.strings.title_about_opensource_repo) },
                    supportingContent = { Text(BuildKonfig.OPENSOURCE_LICENSE) },
                    modifier = Modifier.clickable {
//                    navigator?.push(OpenSourceLicensesScreen)
                        scope.launch {
                            screenState.snackbarHostState
                                .showSnackbar("开源协议")
                        }
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