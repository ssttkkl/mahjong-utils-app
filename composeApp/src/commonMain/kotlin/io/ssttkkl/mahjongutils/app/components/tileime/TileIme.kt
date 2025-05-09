package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.ssttkkl.mahjongdetector.MahjongDetector
import io.ssttkkl.mahjongutils.app.base.components.BackHandler
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.components.clickableButNotFocusable
import io.ssttkkl.mahjongutils.app.components.tile.TileFieldPopMenu
import io.ssttkkl.mahjongutils.app.components.tileime.TileImeHostState.ImeAction
import io.ssttkkl.mahjongutils.app.utils.image.loadAsImage
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_photo_camera
import mahjongutils.models.Tile
import network.chaintech.cmpimagepickncrop.imagecropper.ImageCropResult
import network.chaintech.cmpimagepickncrop.imagecropper.cropImage
import org.jetbrains.compose.resources.vectorResource
import kotlin.time.measureTime

private val tileImeMatrix = listOf(
    Tile.parseTiles("123456789m").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("123456789p").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("123456789s").map { TileImeKey.TileKey(it) },
    Tile.parseTiles("1234567z").map { TileImeKey.TileKey(it) } + TileImeKey.BackspaceKey
)

@Composable
fun TileIme(
    state: TileImeHostState,
    modifier: Modifier = Modifier,
    headerContainer: @Composable (@Composable () -> Unit) -> Unit = { it() },
) {
    val collapsed by remember {
        derivedStateOf { state.specifiedCollapsed ?: state.defaultCollapsed }
    }
    val onLongPress = remember(state) {
        { it: TileImeKey<*> ->
            when (it) {
                is TileImeKey.BackspaceKey -> {
                    state.emitAction(ImeAction.Clear)
                }

                else -> {}
            }
        }
    }
    val onClick = remember(state) {
        { it: TileImeKey<*> ->
            when (it) {
                is TileImeKey.TileKey -> {
                    state.emitAction(ImeAction.Input(listOf(it.tile)))
                }

                is TileImeKey.BackspaceKey -> {
                    state.emitAction(ImeAction.Delete(TileImeHostState.DeleteType.Backspace))
                }
            }
        }
    }

    BackHandler {
        state.specifiedCollapsed = true
    }

    Column(
        modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        headerContainer {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    state.pendingText,
                    Modifier.align(Alignment.Center),
                    MaterialTheme.colorScheme.onSurface
                )

                Image(
                    if (!collapsed)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    "",
                    Modifier
                        .padding(start = 8.dp)
                        .clickableButNotFocusable(remember { MutableInteractionSource() }) {
                            state.specifiedCollapsed = !collapsed
                        }
                        .padding(4.dp)
                        .size(24.dp, 24.dp)
                        .align(Alignment.CenterStart),
                    alignment = Alignment.Center,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )

                Row(
                    Modifier.align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val cropper = LocalImageCropper.current
                    val logger = LoggerFactory.getLogger("MahjongDetector")
                    val picker = rememberFilePickerLauncher(
                        type = FileKitType.Image
                    ) { file ->
                        coroutineScope.launch {
                            runCatching { checkNotNull(file?.loadAsImage()) }
                                .map { originImg ->
                                    val cropResult = cropper.cropImage(bmp = originImg)
                                    if (cropResult is ImageCropResult.Success) {
                                        val res: List<Tile>
                                        val cost = measureTime {
                                            val detections =
                                                MahjongDetector.predict(cropResult.bitmap)
                                            detections.forEach {
                                                logger.debug("detection: ${it}")
                                            }
                                            res = detections
                                                .sortedBy { it.x1 }
                                                .map { Tile[it.className] }
                                        }
                                        logger.info("result: $res, cost: $cost")
                                        state.emitAction(ImeAction.Replace(res))
                                    }
                                }
                                .onFailure { e -> logger.error(e) }
                        }
                    }

                    Image(
                        vectorResource(Res.drawable.icon_photo_camera),
                        "",
                        Modifier
                            .clickableButNotFocusable(remember { MutableInteractionSource() }) {
                                picker.launch()
                            }
                            .padding(4.dp)
                            .size(24.dp, 24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )

                    TileImeDropdownMenu()
                }
            }
        }

        if (!collapsed) {
            KeyboardScreen(
                tileImeMatrix,
                onLongPress,
                onClick
            )
        }
    }
}

@Composable
fun TileImeDropdownMenu(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier) {
        // 触发按钮
        Image(
            Icons.Default.MoreVert,
            "",
            Modifier
                .padding(start = 8.dp)
                .clickableButNotFocusable(remember { MutableInteractionSource() }) {
                    expanded = true
                }
                .padding(4.dp)
                .size(24.dp, 24.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )

        TileFieldPopMenu(expanded, { expanded = !expanded })
    }
}