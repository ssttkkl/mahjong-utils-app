package io.ssttkkl.mahjongutils.app.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch


fun Modifier.tapPress(
    interactionSource: MutableInteractionSource?,
    enabled: Boolean = true,
    onTap: (Offset) -> Unit
): Modifier = if (enabled) composed {
    val scope = rememberCoroutineScope()
    val pressedInteraction = remember { mutableStateOf<PressInteraction.Press?>(null) }
    val onTapState = rememberUpdatedState(onTap)
    DisposableEffect(interactionSource) {
        onDispose {
            pressedInteraction.value?.let { oldValue ->
                val interaction = PressInteraction.Cancel(oldValue)
                interactionSource?.tryEmit(interaction)
                pressedInteraction.value = null
            }
        }
    }
    Modifier.pointerInput(interactionSource) {
        detectTapGestures(
            onPress = {
                scope.launch {
                    // Remove any old interactions if we didn't fire stop / cancel properly
                    pressedInteraction.value?.let { oldValue ->
                        val interaction = PressInteraction.Cancel(oldValue)
                        interactionSource?.emit(interaction)
                        pressedInteraction.value = null
                    }
                    val interaction = PressInteraction.Press(it)
                    interactionSource?.emit(interaction)
                    pressedInteraction.value = interaction
                }
                val success = tryAwaitRelease()
                scope.launch {
                    pressedInteraction.value?.let { oldValue ->
                        val interaction =
                            if (success) {
                                PressInteraction.Release(oldValue)
                            } else {
                                PressInteraction.Cancel(oldValue)
                            }
                        interactionSource?.emit(interaction)
                        pressedInteraction.value = null
                    }
                }
            },
            onTap = { onTapState.value.invoke(it) }
        )
    }
} else this

fun Modifier.clickableButNotFocusable(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
): Modifier {
    return composed {
        this.indication(interactionSource, LocalIndication.current)
            .hoverable(interactionSource)
            .tapPress(interactionSource) {
                onClick()
            }
    }
}

fun Modifier.onEnterKeyDown(
    action: () -> Unit
): Modifier {
    return onKeyEvent {
        if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
            action()
            true
        } else {
            false
        }
    }
}