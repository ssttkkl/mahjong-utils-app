package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import mahjongutils.models.Tile

@Composable
fun FuroShantenScreen(onSubmit: (FuroChanceShantenArgs) -> Unit) {
    Button(onClick = {
        onSubmit(FuroChanceShantenArgs(Tile.parseTiles("1112345678999m"), Tile["9m"]))
    }) {
        Text("Submit")
    }
}
