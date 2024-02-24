package io.ssttkkl.mahjongutils.app.components.tileime

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

class TileImeHostState(
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private val logger = LoggerFactory.getLogger("TileImeHostState")
    }

    enum class DeleteTile {
        Backspace, Delete
    }

    var consumer by mutableStateOf(0)
    val pendingTile = MutableSharedFlow<List<Tile>>()
    val deleteTile = MutableSharedFlow<DeleteTile>()

    val visible by derivedStateOf {
        consumer != 0
    }

    // 通过检测鼠标/触摸来判断默认是否展开软键盘
    // 鼠标：默认不展开
    // 触摸：默认展开
    var defaultCollapsed by mutableStateOf(false)

    // 用户如果点击过折叠按钮，则遵循用户的意图
    var specifiedCollapsed by mutableStateOf<Boolean?>(null)

    inner class TileImeConsumer {
        var consuming by mutableStateOf(false)
            private set

        private var collectPendingTileJob: Job? = null
        private var collectDeleteTileJob: Job? = null

        fun consume(
            handlePendingTile: suspend (List<Tile>) -> Unit,
            handleDeleteTile: suspend (DeleteTile) -> Unit
        ) {
            if (!consuming) {
                consumer += 1
                consuming = true

                collectPendingTileJob = coroutineScope.launch {
                    pendingTile.collect { tile ->
                        handlePendingTile(tile)
                    }
                }

                collectDeleteTileJob = coroutineScope.launch {
                    deleteTile.collect {
                        handleDeleteTile(it)
                    }
                }

                logger.debug("start consuming")
            }
        }

        fun release() {
            if (consuming) {
                consumer -= 1
                consuming = false

                collectPendingTileJob?.cancel()
                collectDeleteTileJob?.cancel()

                logger.debug("stop consuming")
            }
        }
    }

    /**
     * 发送一个退格麻将牌指令（对应Backspace键）
     */
    fun emitBackspaceTile() {
        coroutineScope.launch {
            deleteTile.emit(DeleteTile.Backspace)
        }
    }

    /**
     * 发送一个删除麻将牌指令（对应Delete键）
     */
    fun emitDeleteTile() {
        coroutineScope.launch {
            deleteTile.emit(DeleteTile.Delete)
        }
    }

    /**
     * 发送一个添加麻将牌指令
     */
    fun emitTile(tile: Tile) {
        coroutineScope.launch {
            pendingTile.emit(listOf(tile))
        }
    }

    /**
     * 发送一个添加麻将牌指令
     */
    fun emitTile(tiles: List<Tile>) {
        coroutineScope.launch {
            pendingTile.emit(tiles)
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
            emitTile(tiles)
        }
    }

    fun appendPendingText(str: String) {
        pendingText += str
        tryParsePendingText()
    }

    fun emitBackspacePendingText(count: Int) {
        if (pendingText.length < count) {
            pendingText = ""
        } else {
            pendingText = pendingText.dropLast(count)
            tryParsePendingText()
        }
    }

}