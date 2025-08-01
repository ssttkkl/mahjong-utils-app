package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import io.ssttkkl.mahjongutils.app.base.components.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.components.appscaffold.NoParamUrlNavigationScreen
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.title_about_opensource_licenses
import org.jetbrains.compose.resources.stringResource

@Stable
object OpenSourceLicensesScreen : NoParamUrlNavigationScreen() {
    override val path: String
        get() = "about/licenses"

    override val title: String
        @Composable
        get() = stringResource(Res.string.title_about_opensource_licenses)

    @Composable
    override fun ScreenContent() {
        var aboutlibraries by remember {
            mutableStateOf("")
        }

        LaunchedEffect(Unit) {
            aboutlibraries = Res.readBytes("files/aboutlibraries.json").decodeToString()
        }

        val lazyListState = rememberLazyListState()

        VerticalScrollBox(lazyListState) {
            LibrariesContainer(
                aboutlibraries,
                Modifier.fillMaxSize(),
                lazyListState
            )
        }
    }
}