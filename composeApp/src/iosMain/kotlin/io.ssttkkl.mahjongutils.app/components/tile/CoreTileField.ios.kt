package io.ssttkkl.mahjongutils.app.components.tile

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
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
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.launch
import mahjongutils.models.Tile
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSAttributedString
import platform.Foundation.NSMutableAttributedString
import platform.Foundation.NSSelectorFromString
import platform.Foundation.appendAttributedString
import platform.Foundation.create
import platform.UIKit.NSTextAttachment
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UIFont
import platform.UIKit.UITextField
import platform.UIKit.UITextFieldDelegateProtocol
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
    val coroutineContext = rememberCoroutineScope()

    var prevFocusInteraction by remember { mutableStateOf<FocusInteraction.Focus?>(null) }

    val tileHeight = with(LocalDensity.current) {
        fontSizeInSp.sp.toDp().value.toDouble()
    }

    UIKitView(
        modifier = modifier.fillMaxWidth().height(30.dp),
        factory = {
            val textField = UITextField(CGRectMake(0.0, 0.0, 0.0, 0.0))

            textField.delegate = object : NSObject(), UITextFieldDelegateProtocol {
                override fun textFieldDidBeginEditing(textField: UITextField) {
                    coroutineContext.launch {
                        prevFocusInteraction = FocusInteraction.Focus().also {
                            state.interactionSource.emit(it)
                        }
                    }
                }

                override fun textFieldDidEndEditing(textField: UITextField) {
                    coroutineContext.launch {
                        prevFocusInteraction?.let { prevFocusInteraction ->
                            state.interactionSource.emit(prevFocusInteraction)
                        }
                        prevFocusInteraction = null
                    }
                }

                override fun textFieldDidChangeSelection(textField: UITextField) {
                    val uiRange = textField.selectedTextRange
                    if (uiRange != null) {
                        val start = textField.offsetFromPosition(
                            textField.beginningOfDocument, uiRange.start
                        )
                        val end = textField.offsetFromPosition(
                            textField.beginningOfDocument, uiRange.end
                        )
                        state.selection = TextRange(start.toInt(), end.toInt())
                    } else {
                        state.selection = TextRange.Zero
                    }
                }
            }

            textField.inputView = UIView()
            textField.font = UIFont.systemFontOfSize(tileHeight)

            textField
        },
        update = { textField ->
            textField.text = null
            textField.attributedText = value.toNSAttributedString(tileHeight)

            val start = textField.positionFromPosition(
                textField.beginningOfDocument,
                state.selection.start.toLong()
            )
            val end = textField.positionFromPosition(
                textField.beginningOfDocument,
                state.selection.end.toLong()
            )
            if (start != null && end != null) {
                val uiRange = textField.textRangeFromPosition(start, end)
                textField.setSelectedTextRange(uiRange)
            }
        }
    )
}
