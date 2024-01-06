package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.readTextAsState
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.capture.Capturable
import io.ssttkkl.mahjongutils.app.components.capture.CaptureResult
import io.ssttkkl.mahjongutils.app.components.capture.rememberCaptureState
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen
import kotlinx.coroutines.launch

object AboutScreen : NavigationScreen() {
    override val title: StringResource
        get() = MR.strings.title_about

    @Composable
    override fun Content() {
        val aboutlibraries by MR.files.aboutlibraries.readTextAsState()

        val coroutineScope = rememberCoroutineScope()
        val captureState = rememberCaptureState()
        var captureResult by remember { mutableStateOf<CaptureResult?>(null) }

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
                    aboutlibraries ?: "",
                    Modifier.fillMaxWidth().height(10000.dp)
                )
            }
        }
    }
}