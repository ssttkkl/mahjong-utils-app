package io.ssttkkl.mahjongutils.app.components.tileime

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import mahjongutils.models.Tile

@Stable
class TileImeHostState(
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private val logger = LoggerFactory.getLogger("TileImeHostState")
    }

    enum class DeleteType {
        Backspace, Delete
    }

    sealed class ImeAction {
        data class Input(val data: List<Tile>) : ImeAction()

        data class Delete(val type: DeleteType) : ImeAction()

        data object Copy : ImeAction()

        data object Paste : ImeAction()

        data object Clear : ImeAction()
    }

    var consumer by mutableStateOf(0)
    val pendingAction = MutableSharedFlow<ImeAction>()

    var clipboardData by mutableStateOf<List<Tile>?>(null)
        private set

    fun writeClipboardData(data: List<Tile>?) {
        clipboardData = data
    }

    fun emitAction(action: ImeAction) {
        coroutineScope.launch {
            pendingAction.emit(action)
        }
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
            handleDeleteTile: suspend (DeleteType) -> Unit,
            handleCopyRequest: suspend () -> List<Tile>,
            handleClearRequest: suspend () -> Unit
        ) {
            if (!consuming) {
                consumer += 1
                consuming = true

                collectPendingActionJob = coroutineScope.launch {
                    pendingAction.collect { action ->
                        when (action) {
                            is ImeAction.Input -> handlePendingTile(action.data)
                            ImeAction.Clear -> handleClearRequest()
                            ImeAction.Copy -> writeClipboardData(handleCopyRequest())
                            ImeAction.Paste -> clipboardData?.let { handlePendingTile(it) }
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