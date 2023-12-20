package io.ssttkkl.mahjongutils.app.components.tile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Spannable
import android.text.SpannedString
import android.text.style.ImageSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
                    height,
                    width
                )

                val layer = LayerDrawable(arrayOf(drawable))
                layer.setLayerInset(
                    0,
                    (width * 0.1).toInt(),
                    (height * 0.1).toInt(),
                    (width * 0.1).toInt(),
                    (height * 0.1).toInt()
                )
                layer.setBounds(0, 0, (width * 1.2).toInt(), (height * 1.2).toInt())

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

@SuppressLint("ClickableViewAccessibility")
@Composable
actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
) {
    val coroutineContext = rememberCoroutineScope()

    var notifySelectionChange by remember { mutableStateOf(true) }

    val cursorDrawable = cursorDrawable(cursorColor)
    val tileHeight = with(LocalDensity.current) {
        fontSizeInSp.sp.toPx().toInt()
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            ListenSelectionEditText(context).apply {
                showSoftInputOnFocus = false
                background = null
                setPadding(0, 0, 0, 0)
                minHeight = (tileHeight * 1.2).toInt() // 有20%的LayerInset

                if (Build.VERSION.SDK_INT >= 29) {
                    textCursorDrawable = cursorDrawable
                }

                onFocusChangeListener = object : OnFocusChangeListener {
                    var prevFocusInteraction: FocusInteraction.Focus? = null
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        coroutineContext.launch {
                            if (hasFocus) {
                                prevFocusInteraction = FocusInteraction.Focus().also {
                                    state.interactionSource.emit(it)
                                }
                            } else {
                                prevFocusInteraction?.let { prevFocusInteraction ->
                                    state.interactionSource.emit(FocusInteraction.Unfocus(prevFocusInteraction))
                                }
                                prevFocusInteraction = null
                            }
                        }
                    }
                }

                addOnSelectionChangedListener { selStart, selEnd ->
                    if (notifySelectionChange) {
                        state.selection = TextRange(selStart, selEnd)
                    }
                }

                setOnTouchListener(object : OnTouchListener {
                    var prevPressInteraction: PressInteraction.Press? = null

                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                coroutineContext.launch {
                                    prevPressInteraction = PressInteraction.Press(
                                        Offset(event.x, event.y)
                                    ).also {
                                        state.interactionSource.emit(it)
                                    }
                                }
                            }

                            MotionEvent.ACTION_UP -> {
                                coroutineContext.launch {
                                    prevPressInteraction?.let { prevPressInteraction ->
                                        state.interactionSource.emit(
                                            PressInteraction.Release(prevPressInteraction)
                                        )
                                    }
                                }
                            }

                            MotionEvent.ACTION_CANCEL -> {
                                coroutineContext.launch {
                                    prevPressInteraction?.let { prevPressInteraction ->
                                        state.interactionSource.emit(
                                            PressInteraction.Cancel(prevPressInteraction)
                                        )
                                    }
                                }
                            }
                        }
                        return v.onTouchEvent(event)
                    }
                })
            }
        },
        update = { edittext ->
            edittext.apply {
                // setText的时候会调用onSelectionChanged把选择区域置为[0,0]，所以需要暂时不同步状态
                notifySelectionChange = false
                setText(
                    value.toSpannedString(context, tileHeight)
                )
                setSelection(state.selection.start, state.selection.end)
                notifySelectionChange = true

                if (Build.VERSION.SDK_INT >= 29) {
                    textCursorDrawable = cursorDrawable
                }
            }
        }
    )
}