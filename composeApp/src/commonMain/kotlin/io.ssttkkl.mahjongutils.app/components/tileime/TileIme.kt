package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.skeptick.libres.compose.painterResource
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.utils.Spacing
import io.ssttkkl.mahjongutils.app.utils.emoji
import mahjongutils.models.Tile

@Immutable
sealed class TileImeKey<T : TileImeKey<T>> : KeyboardKeyItem {
    data class TileKey(val tile: Tile) : TileImeKey<TileKey>() {
        @Composable
        override fun display(onClick: () -> Unit) {
            Text(
                tile.emoji,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.small)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable {
                        onClick()
                    },
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            )
        }
    }

    data object BackspaceKey : TileImeKey<BackspaceKey>() {
        override val weightOfRow: Float
            get() = 2f

        @Composable
        override fun display(onClick: () -> Unit) {
            val density = LocalDensity.current

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(Spacing.small)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.secondary)
                    .clickable {
                        onClick()
                    },
                Alignment.Center
            ) {
                Image(
                    Res.image.icon_backspace.painterResource(),
                    Res.string.label_keyboard_backspace,
                    Modifier.size(with(density) { 36.sp.toDp() })
                )
            }
        }
    }
}

@Composable
fun TileIme(
    onCommitTile: (Tile) -> Unit,
    onBackspace: () -> Unit,
    onCollapse: () -> Unit,
) {
    val matrix = listOf(
        Tile.parseTiles("123456789m").map { TileImeKey.TileKey(it) },
        Tile.parseTiles("123456789p").map { TileImeKey.TileKey(it) },
        Tile.parseTiles("123456789s").map { TileImeKey.TileKey(it) },
        Tile.parseTiles("1234567z").map { TileImeKey.TileKey(it) } + TileImeKey.BackspaceKey
    )
    KeyboardScreen(matrix, onCollapse) {
        when (it) {
            is TileImeKey.TileKey -> {
                onCommitTile(it.tile)
            }

            is TileImeKey.BackspaceKey -> {
                onBackspace()
            }
        }
    }
}