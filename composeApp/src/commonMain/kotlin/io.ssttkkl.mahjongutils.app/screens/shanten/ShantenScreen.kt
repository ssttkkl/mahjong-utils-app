package io.ssttkkl.mahjongutils.app.screens.shanten

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.navigator.RouteInfo
import io.ssttkkl.mahjongutils.app.utils.TypedValue
import mahjongutils.models.Tile
import kotlin.reflect.KType

@Composable
fun ShantenScreen(onSubmit: (ShantenArgs) -> Unit) {
    Button(onClick = {
        onSubmit(ShantenArgs(Tile.parseTiles("11123456789999m")))
    }) {
        Text("Submit")
    }
}
