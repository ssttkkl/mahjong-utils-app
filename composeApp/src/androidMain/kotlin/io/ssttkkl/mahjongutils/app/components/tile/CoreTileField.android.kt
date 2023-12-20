package io.ssttkkl.mahjongutils.app.components.tile

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Spannable
import android.text.SpannedString
import android.text.style.ImageSpan
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.buildSpannedString
import kotlinx.coroutines.launch
import mahjongutils.models.Tile


private fun List<Tile>.toSpannedString(context: Context, height: Int): SpannedString {
    return buildSpannedString {
        this@toSpannedString.forEach {
            val text = "p"
            val start = this.length
            val end = start + text.length

            append(text)

            val drawable = it.imageResource.getDrawable(context) as? BitmapDrawable
            if (drawable != null) {
                // 牌的比例是1.4:1
                val width = (height / 1.4).toInt()

                drawable.setBounds(
                    0,
                    0,
                    (height * 0.8).toInt(),
                    (width * 0.8).toInt()
                )

                val layer = LayerDrawable(arrayOf(drawable))
                layer.setLayerInset(
                    0,
                    (width * 0.1).toInt(),
                    (height * 0.1).toInt(),
                    (width * 0.1).toInt(),
                    (height * 0.1).toInt()
                )
                layer.setBounds(0, 0, width, height)

                setSpan(
                    ImageSpan(layer),
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}

@Composable
fun cursorDrawable(cursorColor: Color): GradientDrawable {
    val thickness = with(LocalDensity.current) {
        2.dp.toPx().toInt()
    }

    return remember {
        GradientDrawable()
    }.apply {
        setColor(cursorColor.toArgb())
        setSize(thickness, 0)
    }
}

@Composable
actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
) {
    val coroutineContext = rememberCoroutineScope()

    var pendingSelectionChange by remember { mutableStateOf(false) }
    var prevFocusInteraction by remember { mutableStateOf<FocusInteraction.Focus?>(null) }

    val cursorDrawable = cursorDrawable(cursorColor)
    val tileHeight = with(LocalDensity.current) {
        fontSizeInSp.sp.toPx().toInt()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            ListenSelectionEditText(context).apply {
                showSoftInputOnFocus = false
                textSize = fontSizeInSp
                background = null

                if (Build.VERSION.SDK_INT >= 29) {
                    textCursorDrawable = cursorDrawable
                }

                setOnFocusChangeListener { _, focused ->
                    coroutineContext.launch {
                        if (focused) {
                            prevFocusInteraction = FocusInteraction.Focus().also {
                                state.interactionSource.emit(it)
                            }
                        } else {
                            prevFocusInteraction?.let { prevFocusInteraction ->
                                state.interactionSource.emit(prevFocusInteraction)
                            }
                            prevFocusInteraction = null
                        }
                    }
                }

                addOnSelectionChangedListener { selStart, selEnd ->
                    if (!pendingSelectionChange) {
                        state.selection = TextRange(selStart, selEnd)
                    }
                }
            }
        },
        update = { edittext ->
            edittext.apply {
                // setText的时候会调用onSelectionChanged把选择区域置为[0,0)，所以需要暂时不同步状态
                pendingSelectionChange = true
                setText(
                    value.toSpannedString(context, tileHeight)
                )
                setSelection(state.selection.start, state.selection.end)
                pendingSelectionChange = false

                if (Build.VERSION.SDK_INT >= 29) {
                    textCursorDrawable = cursorDrawable
                }
            }
        }
    )
}