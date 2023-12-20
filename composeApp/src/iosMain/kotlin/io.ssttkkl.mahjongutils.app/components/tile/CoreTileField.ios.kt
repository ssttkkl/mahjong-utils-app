package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.launch
import mahjongutils.models.Tile
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSAttributedString
import platform.Foundation.NSMutableAttributedString
import platform.Foundation.appendAttributedString
import platform.UIKit.NSTextAttachment
import platform.UIKit.UIFont
import platform.UIKit.UITextView
import platform.UIKit.UITextViewDelegateProtocol
import platform.UIKit.UIView
import platform.UIKit.attributedStringWithAttachment
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
private fun List<Tile>.toNSAttributedString(height: Double): NSAttributedString {
    val width = height / 1.4  // 牌的比例是1.4:1

    val attributedString = NSMutableAttributedString()

    forEach {
        val attachment = NSTextAttachment()
        attachment.image = it.imageResource.toUIImage()
        attachment.bounds = CGRectMake(0.0, 0.0, width, height)

        val attrStringWithImage = NSAttributedString.attributedStringWithAttachment(attachment)
        attributedString.appendAttributedString(attrStringWithImage)
    }

    return attributedString
}

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun CoreTileField(
    value: List<Tile>,
    modifier: Modifier,
    state: CoreTileFieldState,
    cursorColor: Color,
    fontSizeInSp: Float
) {
    val scope = currentRecomposeScope
    val coroutineContext = rememberCoroutineScope()

    var notifySelectionChange by remember { mutableStateOf(true) }
    var prevFocusInteraction by remember { mutableStateOf<FocusInteraction.Focus?>(null) }
    var prevPressInteraction by remember { mutableStateOf<PressInteraction.Press?>(null) }

    val tileHeight = with(LocalDensity.current) {
        fontSizeInSp.sp.toDp().value.toDouble()
    }

    // 防止被GC，在这里强引用
    val textViewDelegate = remember {
        object : NSObject(), UITextViewDelegateProtocol {
            override fun textViewDidChange(textView: UITextView) {
                val textShouldBe = value.toNSAttributedString(tileHeight)
                if (!textView.attributedText.isEqual(textShouldBe)) {
                    // 防止用户输入了什么奇怪的东西，重新渲染下
                    scope.invalidate()
                }
            }

            override fun textViewDidBeginEditing(textView: UITextView) {
                coroutineContext.launch {
                    prevFocusInteraction = FocusInteraction.Focus().also {
                        state.interactionSource.emit(it)
                    }
                }
            }

            override fun textViewDidEndEditing(textView: UITextView) {
                coroutineContext.launch {
                    prevFocusInteraction?.let { prevFocusInteraction ->
                        state.interactionSource.emit(prevFocusInteraction)
                    }
                    prevFocusInteraction = null
                }
            }

            override fun textViewDidChangeSelection(textView: UITextView) {
                if (notifySelectionChange) {
                    val uiRange = textView.selectedTextRange
                    if (uiRange != null) {
                        val start = textView.offsetFromPosition(
                            textView.beginningOfDocument, uiRange.start
                        )
                        val end = textView.offsetFromPosition(
                            textView.beginningOfDocument, uiRange.end
                        )
                        state.selection = TextRange(start.toInt(), end.toInt())
                    } else {
                        state.selection = TextRange.Zero
                    }
                }
            }
        }
    }

    UIKitView(
        modifier = modifier.fillMaxWidth().height((tileHeight * 1.2).dp),
        factory = {
            UITextView(CGRectMake(0.0, 0.0, 0.0, 0.0)).apply {
                textContainer.maximumNumberOfLines = 1u
                inputView = UIView()
                font = UIFont.systemFontOfSize(tileHeight)
                delegate = textViewDelegate
            }
        },
        update = {
            it.apply {
                // setText的时候会调用onSelectionChanged把选择区域置为[n,n]，所以需要暂时不同步状态
                notifySelectionChange = false
                attributedText = value.toNSAttributedString(tileHeight)
                notifySelectionChange = true

                val start = positionFromPosition(
                    beginningOfDocument,
                    state.selection.start.toLong()
                )
                val end = positionFromPosition(
                    beginningOfDocument,
                    state.selection.end.toLong()
                )
                if (start != null && end != null) {
                    val uiRange = textRangeFromPosition(start, end)
                    setSelectedTextRange(uiRange)
                }
            }
        }
    )
}
