package io.ssttkkl.mahjongutils.app.components.capturablelazy

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

class FakeLazyColumnScope(
    val content: LazyListScope.() -> Unit
): LazyListScope {
    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit
    ) {
        error("The method is not implemented")
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit
    ) {
        error("The method is not implemented")
    }

    @ExperimentalFoundationApi
    override fun stickyHeader(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit
    ) {
        error("The method is not implemented")
    }

    @Composable
    fun apply() {

    }
}