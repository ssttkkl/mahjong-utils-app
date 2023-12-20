package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import dev.icerock.moko.resources.StringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.capture.Capturable
import io.ssttkkl.mahjongutils.app.components.capture.rememberCaptureState
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
            val coroutineScope = rememberCoroutineScope()
            val captureState = rememberCaptureState()
            var captureResult by remember { mutableStateOf<ImageBitmap?>(null) }

            Column {
                FloatingActionButton({
                    coroutineScope.launch {
                        captureResult = captureState.capture()
                    }
                }) {
                    Icon(Icons.Outlined.Share, "")
                }

                Capturable(captureState, heightWrapContent = true) {
                    LibrariesContainer(
                        aboutlibraries,
                        Modifier.fillMaxWidth().height(10000.dp)
                    )
                }
            }
        }
    }
}