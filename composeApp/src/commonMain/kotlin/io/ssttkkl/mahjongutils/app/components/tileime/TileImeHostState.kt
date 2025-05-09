package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import io.ssttkkl.mahjongutils.app.base.utils.LoggerFactory
import io.ssttkkl.mahjongutils.app.base.utils.getText
import io.ssttkkl.mahjongutils.app.base.utils.setText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import mahjongutils.models.Tile
import mahjongutils.models.toTilesString

@Stable
class TileImeHostState(
    private val coroutineScope: CoroutineScope,
    private val clipboardManager: Clipboard,
) {
    companion object {
        private val logger = LoggerFactory.getLogger("TileImeHostState")
    }

    enum class DeleteType {
        Backspace, Delete
    }

    sealed class ImeAction {
        data class Input(val data: List<Tile>) : ImeAction()

        data class Replace(val data: List<Tile>) : ImeAction()

        data class Delete(val type: DeleteType) : ImeAction()

        data object Copy : ImeAction()

        data object Paste : ImeAction()

        data object Clear : ImeAction()
    }

    var consumer by mutableStateOf(0)
    val pendingAction = MutableSharedFlow<ImeAction>(extraBufferCapacity = 255)

    suspend fun readClipboardData(): List<Tile>? {
        return clipboardManager.getText()?.let {
            runCatching { Tile.parseTiles(it) }.getOrNull()
        }
    }

    suspend fun writeClipboardData(data: List<Tile>?) {
        data?.let {
            clipboardManager.setText(it.toTilesString())
        }
    }

    fun emitAction(action: ImeAction) {
        pendingAction.tryEmit(action)
    }

    val visible by derivedStateOf {
        consumer != 0
    }

    // 通过检测鼠标/触摸来判断默认是否展开软键盘
    // 鼠标：默认不展开
    // 触摸：默认展开
    var defaultCollapsed by mutableStateOf(false)

    // 用户如果点击过折叠按钮，则遵循用户的意图
    var specifiedCollapsed by mutableStateOf<Boolean?>(null)

    @Stable
    inner class TileImeConsumer {
        var consuming by mutableStateOf(false)
            private set

        private var collectPendingActionJob: Job? = null

        fun consume(
            handlePendingTile: suspend (List<Tile>) -> Unit,
            handleReplaceTile: suspend (List<Tile>) -> Unit,
            handleDeleteTile: suspend (DeleteType) -> Unit,
            handleCopyRequest: suspend () -> List<Tile>,
            handleClearRequest: suspend () -> Unit
        ) {
            if (!consuming) {
                consumer += 1
                consuming = true

                collectPendingActionJob = coroutineScope.launch(Dispatchers.Main) {
                    pendingAction.collect { action ->
                        when (action) {
                            is ImeAction.Input -> handlePendingTile(action.data)
                            is ImeAction.Replace -> handleReplaceTile(action.data)
                            ImeAction.Clear -> handleClearRequest()
                            ImeAction.Copy -> writeClipboardData(handleCopyRequest())
                            ImeAction.Paste -> readClipboardData()?.let { handlePendingTile(it) }
                            is ImeAction.Delete -> handleDeleteTile(action.type)
                        }
                    }
                }

                logger.debug("start consuming")
            }
        }

        fun release() {
            if (consuming) {
                consumer -= 1
                consuming = false

                collectPendingActionJob?.cancel()

                logger.debug("stop consuming")
            }
        }
    }

    /**
     * 用户通过键盘输入的文本，待解析为麻将牌
     */
    var pendingText: String by mutableStateOf("")
        private set

    private fun tryParsePendingText() {
        val tiles = try {
            Tile.parseTiles(pendingText)
        } catch (e: IllegalArgumentException) {
            null
        }

        if (tiles != null) {
            pendingText = ""
            emitAction(ImeAction.Input(tiles))
        }
    }

    fun appendPendingText(str: String) {
        pendingText += str
        tryParsePendingText()
    }

    fun removeLastPendingText(count: Int) {
        if (pendingText.length < count) {
            pendingText = ""
        } else {
            pendingText = pendingText.dropLast(count)
            tryParsePendingText()
        }
    }
}

@Composable
fun rememberTileImeHostState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    clipboardManager: Clipboard = LocalClipboard.current
): TileImeHostState {
    return remember { TileImeHostState(coroutineScope, clipboardManager) }
}