package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mahjongutils.shanten.UnionShantenResult

@Composable
fun ShantenResultScreen(args: ShantenArgs) {
    var result by remember { mutableStateOf<UnionShantenResult?>(null) }
    LaunchedEffect(args) {
        result = withContext(Dispatchers.Default) {
            args.calc()
        }
    }
    if (result != null) {
        Text(result.toString())
    }
}