package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.FocusInteraction
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.launch
import mahjongutils.models.Tile
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSAttributedString
import platform.Foundation.NSMutableAttributedString
import platform.Foundation.appendAttributedString
import platform.UIKit.NSTextAttachment
import platform.UIKit.UIColor
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIFont
import platform.UIKit.UIKeyboardTypeEmailAddress
import platform.UIKit.UILineBreakModeCharacterWrap
import platform.UIKit.UITextAutocorrectionType
import platform.UIKit.UITextSpellCheckingType
import platform.UIKit.UITextView
import platform.UIKit.UITextViewDelegateProtocol
import platform.UIKit.UIView
import platform.UIKit.attributedStringWithAttachment
import platform.darwin.NSObject
import kotlin.math.max

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
    val focusManager = LocalFocusManager.current

    var notifySelectionChange by remember { mutableStateOf(true) }

    val tileHeight = with(LocalDensity.current) {
        fontSizeInSp.sp.toDp().value.toDouble()
    }
    var heightOfText by remember { mutableStateOf(0.0) }

    // 防止被GC，在这里强引用
    val textViewDelegate = remember {
        object : NSObject(), UITextViewDelegateProtocol {
            var prevFocusInteraction: FocusInteraction.Focus? = null

            override fun textViewDidChange(textView: UITextView) {
                val textShouldBe = value.toNSAttributedString(tileHeight)
                if (!textView.attributedText.isEqual(textShouldBe)) {
                    // 防止用户输入了什么奇怪的东西，重新渲染下
                    scope.invalidate()
                }
            }

            override fun textViewDidBeginEditing(textView: UITextView) {
                focusManager.clearFocus()  // 如果当前焦点在compose编辑框，不会自动清除焦点
                prevFocusInteraction = FocusInteraction.Focus()
                    .also {
                        coroutineContext.launch {
                            state.interactionSource.emit(it)
                        }
                    }
            }

            override fun textViewDidEndEditing(textView: UITextView) {
                prevFocusInteraction?.let { prevFocusInteraction ->
                    coroutineContext.launch {
                        state.interactionSource.emit(FocusInteraction.Unfocus(prevFocusInteraction))
                    }
                }
                prevFocusInteraction = null
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
        modifier = modifier.fillMaxWidth()
            .height(max(tileHeight * 1.2, heightOfText).dp),  // 最小高度为tileHeight * 1.2，避免高度跳动
        factory = {
            UITextView().apply {
                font = UIFont.systemFontOfSize(tileHeight)

                // 去掉内部的padding
                textContainerInset = UIEdgeInsetsMake(0.0, 0.0, 0.0, 0.0)
                textContainer.lineBreakMode = UILineBreakModeCharacterWrap
                textContainer.lineFragmentPadding = 0.0

                // 关闭输入法以及自动更正
                keyboardType = UIKeyboardTypeEmailAddress
                inputView = UIView()
                autocorrectionType = UITextAutocorrectionType.UITextAutocorrectionTypeNo
                spellCheckingType = UITextSpellCheckingType.UITextSpellCheckingTypeNo

                delegate = textViewDelegate
            }
        },
        update = {
            it.apply {
                // setText的时候会调用onSelectionChanged把选择区域置为[n,n]，所以需要暂时不同步状态
                notifySelectionChange = false
                attributedText = value.toNSAttributedString(tileHeight)
                notifySelectionChange = true

                // 设置光标位置
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
                    scrollRangeToVisible(selectedRange)
                }
                tintColor = UIColor(
                    red = cursorColor.red.toDouble(),
                    green = cursorColor.green.toDouble(),
                    blue = cursorColor.blue.toDouble(),
                    alpha = cursorColor.alpha.toDouble()
                )

                // 计算合适的高度
                val sizeThatFitsTextView = sizeThatFits(
                    CGSizeMake(frame.useContents { size.width }, Double.MAX_VALUE)
                )
                heightOfText = sizeThatFitsTextView.useContents { height }
            }
        }
    )
}
