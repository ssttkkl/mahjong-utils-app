package io.ssttkkl.mahjongutils.app.components.tile

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

class SpacingImageSpan(drawable: Drawable, val paddingHorizontal: Float) : ImageSpan(drawable) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        canvas.translate(paddingHorizontal, 0f)
        super.draw(canvas, text, start, end, x, top, y, bottom, paint)
        canvas.translate(paddingHorizontal, 0f)
        canvas.restore()
    }
}