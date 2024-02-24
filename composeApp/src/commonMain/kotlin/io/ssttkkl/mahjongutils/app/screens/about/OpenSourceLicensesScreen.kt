package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import io.ssttkkl.mahjongutils.app.components.scrollbox.VerticalScrollBox
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.title_about_opensource_licenses
import org.jetbrains.compose.resources.StringResource

object OpenSourceLicensesScreen : NavigationScreen() {
    override val title: StringResource?
        get() = Res.string.title_about_opensource_licenses

    @Composable
    override fun Content() {
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