@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package io.ssttkkl.mahjongutils.app.components.capturablelazy

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.CaptureController.CaptureRequest
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalComposeApi::class
)
@Composable
fun LazyCapturableColumn(
    captureController: CaptureController,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: LazyListScope.() -> Unit
) {
    var captureRequest: CaptureRequest? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    val innerCaptureController = rememberCaptureController()
    var onDrawSignal: CompletableDeferred<Unit>? by remember {
        mutableStateOf(null)
    }

    DisposableEffect(captureController) {
        val job = coroutineScope.launch {
            captureController.captureRequests.collect {
                captureRequest = it
                onDrawSignal = CompletableDeferred()
            }
        }

        onDispose {
            job.cancel()
        }
    }

    captureRequest?.let { req ->
        Box(modifier = Modifier.requiredHeight(10000.dp)) {
            LazyColumn(
                modifier.capturable(innerCaptureController)
                    .drawWithContent {
                        drawContent()
                        onDrawSignal?.complete(Unit)
                    },
                state,
                contentPadding,
                reverseLayout,
                verticalArrangement,
                horizontalAlignment,
                flingBehavior,
                userScrollEnabled,
                content
            )
        }
        DisposableEffect(req) {
            val job = coroutineScope.launch {
                val result = runCatching {
                    onDrawSignal?.await()
                    onDrawSignal = null
                    innerCaptureController.captureAsync(req.config).await()
                }
                req.imageBitmapDeferred.completeWith(result)
                captureRequest = null
            }

            onDispose {
                job.cancel()
            }
        }
    } ?: run {
        LazyColumn(
            modifier,
            state,
            contentPadding,
            reverseLayout,
            verticalArrangement,
            horizontalAlignment,
            flingBehavior,
            userScrollEnabled,
            content
        )
    }
}