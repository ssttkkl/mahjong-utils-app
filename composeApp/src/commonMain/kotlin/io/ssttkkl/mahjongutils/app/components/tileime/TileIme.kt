package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    collapsed: Boolean,
    onCommitTile: (Tile) -> Unit,
    onBackspace: () -> Unit,
    onChangeCollapsed: (Boolean) -> Unit,
) {
    val currentOnCommitTile by rememberUpdatedState(onCommitTile)
    val currentOnBackspace by rememberUpdatedState(onBackspace)
    val currentOnChangeCollapsed by rememberUpdatedState(onChangeCollapsed)
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
        currentOnChangeCollapsed(true)
    }

    Column(
        Modifier.fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.4f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            if (!collapsed)
                Icons.Filled.KeyboardArrowDown
            else
                Icons.Filled.KeyboardArrowUp,
            "",
            Modifier.clickableButNotFocusable(remember { MutableInteractionSource() }) {
                onChangeCollapsed(!collapsed)
            }.fillMaxWidth()
                .padding(4.dp)
                .height(24.dp),
            alignment = Alignment.Center,
        )

        HorizontalDivider(Modifier)

        if (pendingText.isNotEmpty()) {
            Text(pendingText, Modifier.padding(vertical = 8.dp))
        }

        if (!collapsed) {
            Spacer(Modifier.height(8.dp))

            KeyboardScreen(
                tileImeMatrix,
                currentOnCommit
            )
        }
    }
}