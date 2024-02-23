package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ssttkkl.mahjongutils.app.components.backhandler.BackHandler
import io.ssttkkl.mahjongutils.app.components.clickableButNotFocusable
import io.ssttkkl.mahjongutils.app.components.tile.painterResource
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_backspace
import mahjongutils.models.Tile
import org.jetbrains.compose.resources.painterResource

@Immutable
sealed class TileImeKey<T : TileImeKey<T>> : KeyboardKeyItem {
    data class TileKey(val tile: Tile) : TileImeKey<TileKey>() {
        @Composable
        override fun display(onClick: () -> Unit) {
            val density = LocalDensity.current

            val interactionSource = remember { MutableInteractionSource() }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickableButNotFocusable(interactionSource) {
                        onClick()
                    },
                Alignment.Center
            ) {
                Image(
                    tile.painterResource,
                    "",
                    Modifier.size(with(density) { 36.sp.toDp() })
                )
            }
        }
    }

    data object BackspaceKey : TileImeKey<BackspaceKey>() {
        override val weightOfRow: Float
            get() = 2f

        @Composable
        override fun display(onClick: () -> Unit) {
            val density = LocalDensity.current

            val interactionSource = remember { MutableInteractionSource() }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickableButNotFocusable(interactionSource) {
                        onClick()
                    },
                Alignment.Center
            ) {
                Image(
                    painterResource(Res.drawable.icon_backspace),
                    "",
                    Modifier.size(with(density) { 36.sp.toDp() }),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                )
            }
        }
    }
}

private val tileImeMatrix = listOf(
    Tile.parseTiles("123456789m").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("123456789p").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("123456789s").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("1234567z").map { TileImeKey.TileKey(it) } + TileImeKey.BackspaceKey
)

@Composable
fun TileIme(
    pendingText: String,
    onCommitTile: (Tile) -> Unit,
    onBackspace: () -> Unit,
    onCollapse: () -> Unit,
) {
    val currentOnCommitTile by rememberUpdatedState(onCommitTile)
    val currentOnBackspace by rememberUpdatedState(onBackspace)
    val currentOnCollapse by rememberUpdatedState(onCollapse)
    val currentOnCommit = remember {
        { it: TileImeKey<*> ->
            when (it) {
                is TileImeKey.TileKey -> {
                    currentOnCommitTile(it.tile)
                }

                is TileImeKey.BackspaceKey -> {
                    currentOnBackspace()
                }
            }
        }
    }

    BackHandler {
        currentOnCollapse()
    }
    KeyboardScreen(
        tileImeMatrix,
        pendingText,
        currentOnCollapse,
        currentOnCommit
    )
}