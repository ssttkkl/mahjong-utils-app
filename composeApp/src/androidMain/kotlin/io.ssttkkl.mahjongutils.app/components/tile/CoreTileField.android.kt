package io.ssttkkl.mahjongutils.app.components.tile

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Spannable
import android.text.SpannedString
import android.text.style.ImageSpan
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.buildSpannedString
import mahjongutils.models.Tile

private fun dp2Px(dp: Float, ctx: Context): Int {
    return if (Build.VERSION.SDK_INT >= 34) {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.resources.displayMetrics)
            .toInt()
    } else {
        val scale = ctx.resources.displayMetrics.density
        (dp * scale + 0.5f).toInt()
    }
}

private fun sp2Px(sp: Float, ctx: Context): Int {
    return if (Build.VERSION.SDK_INT >= 34) {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, ctx.resources.displayMetrics)
            .toInt()
    } else {
        val fontScale = ctx.resources.displayMetrics.scaledDensity
        (sp * fontScale + 0.5f).toInt()
    }
}


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
actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float,
) {
    var pendingSelectionChange by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            ListenSelectionEditText(context).apply {
                showSoftInputOnFocus = false
                maxLines = 1
                textSize = fontSizeInSp
                background = null

                if (Build.VERSION.SDK_INT >= 29) {
                    textCursorDrawable = GradientDrawable().apply {
                        setColor(cursorColor.toArgb())
                        setSize(dp2Px(2f, context), 0)
                    }
                }

                setOnFocusChangeListener { view, b ->
                    state.focused = b
                }

                addOnSelectionChangedListener { selStart, selEnd ->
                    if (!pendingSelectionChange) {
                        state.selection = TextRange(selStart, selEnd)
                    }
                }
            }
        },
        update = {
            // setText的时候会调用onSelectionChanged把选择区域置为[0,0)，所以需要暂时不同步状态
            pendingSelectionChange = true
            it.setText(
                value.toSpannedString(
                    it.context,
                    sp2Px(it.textSize, it.context)
                )
            )
            it.setSelection(state.selection.start, state.selection.end)
            pendingSelectionChange = false

            if (Build.VERSION.SDK_INT >= 29) {
                (it.textCursorDrawable as? GradientDrawable)?.setColor(cursorColor.toArgb())
            }
        }
    )
}