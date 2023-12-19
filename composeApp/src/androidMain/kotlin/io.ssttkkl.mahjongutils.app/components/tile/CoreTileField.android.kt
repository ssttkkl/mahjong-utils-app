package io.ssttkkl.mahjongutils.app.components.tile

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.SpanWatcher
import android.text.Spannable
import android.text.Spanned
import android.text.SpannedString
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.TypedValue
import android.widget.EditText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.buildSpannedString
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
            val text = it.toString()
            val start = this.length
            val end = start + text.length

            append(text)

            val drawable = it.imageResource.getDrawable(context)
            if (drawable != null) {
                drawable.setBounds(
                    0,
                    0,
                    spToPx(textSize, context),
                    (spToPx(textSize, context) * 1.4).toInt()  // 牌的比例是1.4:1
                )
                setSpan(
                    ImageSpan(drawable, ImageSpan.ALIGN_BASELINE),
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
    AndroidView(
        modifier = modifier,
        factory = { context ->
            EditText(context).apply {
                showSoftInputOnFocus = false
                maxLines = 1
                textSize = 30f

                setOnFocusChangeListener { view, b ->
                    state.focused = b
                }

                val spanWatcher = object : SpanWatcher {
                    override fun onSpanChanged(
                        text: Spannable, what: Any,
                        ostart: Int, oend: Int, nstart: Int, nend: Int
                    ) {
                        if (what === Selection.SELECTION_START || what === Selection.SELECTION_END) {
                            state.selection = TextRange(nstart, nend)
                        }
                    }

                    override fun onSpanAdded(
                        text: Spannable, what: Any,
                        start: Int, end: Int
                    ) {
                        // Nothing here.
                    }

                    override fun onSpanRemoved(
                        text: Spannable, what: Any,
                        start: Int, end: Int
                    ) {
                        // Nothing here.
                    }
                }

                val textWatcher = object : TextWatcher {
                    override fun afterTextChanged(e: Editable?) {
                        e?.setSpan(spanWatcher, 0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        // Nothing here.
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                }

                addTextChangedListener(textWatcher)
            }
        },
        update = {
            it.setText(value.toSpannedString(it.context, it.textSize))

            it.setSelection(state.selection.start, state.selection.end)
        }
    )
}