package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

object AboutScreen : NavigationScreen {
    override val title: StringResource
        get() = MR.strings.title_about

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        Calculation(
            Unit,
            {
                resource("aboutlibraries.json").readBytes().decodeToString()
            },
            onFailure = { thr ->
                val coroutineScope = rememberCoroutineScope()

                val snackbarHostState = LocalAppState.current.snackbarHostState

                LaunchedEffect(true) {
                    thr.printStackTrace()

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            thr.message ?: thr.toString()
                        )
                    }
                }
            }
        ) { aboutlibraries ->
            LibrariesContainer(
                aboutlibraries,
                Modifier.fillMaxSize()
            )
        }
    }
}