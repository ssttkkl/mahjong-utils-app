package io.ssttkkl.mahjongutils.app.components.tile

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

internal class ListenSelectionEditText : EditText {
    fun interface OnSelectionChangedListener {
        fun onSelectionChanged(selStart: Int, selEnd: Int)
    }

    private val onSelectionChangedListeners: MutableList<OnSelectionChangedListener> = ArrayList()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        // 子类还没初始化的时候父类调用，此时onSelectionChangedListeners == null
        if (onSelectionChangedListeners != null) {
            for (listener in onSelectionChangedListeners) {
                listener.onSelectionChanged(selStart, selEnd)
            }
        }
    }

    fun addOnSelectionChangedListener(listener: OnSelectionChangedListener) {
        onSelectionChangedListeners.add(listener)
    }

    fun removeOnSelectionChangedListener(listener: OnSelectionChangedListener) {
        onSelectionChangedListeners.remove(listener)
    }
}