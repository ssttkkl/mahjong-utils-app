package io.ssttkkl.mahjongutils.app.components.calculation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.text_unknown_error
import org.jetbrains.compose.resources.stringResource
import kotlin.coroutines.CoroutineContext

@Composable
fun PopAndShowSnackbarOnFailure(throwable: Throwable) {
    val appState = LocalAppState.current
    val unknownError = stringResource(Res.string.text_unknown_error)

    LaunchedEffect(throwable) {
        appState.navigator.popUntilRoot()
        withContext(NonCancellable) {
            appState.snackbarHostState.showSnackbar(unknownError)
        }
    }
}

@Composable
fun ShowSnackbarOnFailure(throwable: Throwable) {
    val appState = LocalAppState.current
    val unknownError = stringResource(Res.string.text_unknown_error)

    LaunchedEffect(throwable) {
        withContext(NonCancellable) {
            appState.snackbarHostState.showSnackbar(unknownError)
        }
    }
}

@Composable
fun <U, V> Calculation(
    arg: U,
    handle: suspend (U) -> V,
    context: CoroutineContext = Dispatchers.Default,
    onCalculating: @Composable () -> Unit = {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    },
    onFailure: @Composable ((Throwable) -> Unit) = { ShowSnackbarOnFailure(it) },
    onSuccess: @Composable (V) -> Unit
) {
    var result: Result<V>? by remember(arg) { mutableStateOf(null) }
    LaunchedEffect(arg) {
        result = runCatching {
            withContext(context) {
                handle(arg)
            }
        }
    }

    result?.let {
        it.onSuccess { onSuccess(it) }
            .onFailure { onFailure(it) }
    } ?: onCalculating()
}
