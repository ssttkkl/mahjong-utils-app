package io.ssttkkl.mahjongutils.app.components.tile

import android.content.Context
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import io.ssttkkl.mahjongutils.app.R
import mahjongutils.models.Tile

private fun spToPx(sp: Float, ctx: Context): Int {
    return if (Build.VERSION.SDK_INT >= 34) {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, sp, ctx.resources.displayMetrics)
            .toInt()
    } else {
        (sp * ctx.resources.displayMetrics.scaledDensity).toInt()
    }
}


private fun List<Tile>.toSpannedString(context: Context, textSize: Float): SpannedString {
    return buildSpannedString {
        this@toSpannedString.forEach {
            val text = "p"
            val start = this.length
            val end = start + text.length

            append(text)

            val drawable = it.imageResource.getDrawable(context) as? BitmapDrawable
            if (drawable != null) {
                // 牌的比例是1.4:1
                val height = spToPx(textSize, context)
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
    label: String?,
    isError: Boolean
) {
    var pendingSelectionChange by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            ListenSelectionEditText(context).apply {
                showSoftInputOnFocus = false
                maxLines = 1
                textSize = 30f

                setOnFocusChangeListener { view, b ->
                    state.focused = b
                }

                addOnSelectionChangedListener { selStart, selEnd ->
                    if (!pendingSelectionChange) {
                        state.selection = TextRange(selStart, selEnd)
                    }
                }

//                val spanWatcher = object : SpanWatcher {
//                    override fun onSpanChanged(
//                        text: Spannable, what: Any,
//                        ostart: Int, oend: Int, nstart: Int, nend: Int
//                    ) {
//                        if (what === Selection.SELECTION_START || what === Selection.SELECTION_END) {
//                            state.selection = TextRange(nstart, nend)
//                        }
//                    }
//
//                    override fun onSpanAdded(
//                        text: Spannable, what: Any,
//                        start: Int, end: Int
//                    ) {
//                        // Nothing here.
//                    }
//
//                    override fun onSpanRemoved(
//                        text: Spannable, what: Any,
//                        start: Int, end: Int
//                    ) {
//                        // Nothing here.
//                    }
//                }
//
//                val textWatcher = object : TextWatcher {
//                    override fun afterTextChanged(e: Editable?) {
//                        e?.setSpan(spanWatcher, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//                    }
//
//                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                        // Nothing here.
//                    }
//
//                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    }
//                }
//
//                addTextChangedListener(textWatcher)
            }
        },
        update = {
            // setText的时候会调用onSelectionChanged把选择区域置为[0,0)，所以需要暂时不同步状态
            pendingSelectionChange = true
            it.setText(value.toSpannedString(it.context, it.textSize))
            it.setSelection(state.selection.start, state.selection.end)
            pendingSelectionChange = false
        }
    )
}