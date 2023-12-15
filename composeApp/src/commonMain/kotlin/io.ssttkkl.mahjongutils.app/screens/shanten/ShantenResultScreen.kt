package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.LocalAppState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mahjongutils.shanten.CommonShantenResult

@Composable
fun ShantenResultScreen(args: ShantenArgs) {
    val appState = LocalAppState.current
    var result by remember { mutableStateOf<CommonShantenResult<*>?>(null) }
    LaunchedEffect(args) {
        try {
            result = withContext(Dispatchers.Default) {
                args.calc()
            }
        } catch (e: Exception) {
            appState.scaffoldState.snackbarHostState.showSnackbar(
                e.message ?: e::class.simpleName ?: "ERROR"
            )
        }
    }
    if (result != null) {
        Text(result.toString())
    }
}