package io.ssttkkl.mahjongutils.app.components.ratio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class RatioOption<T>(
    val value: T,
    val title: String,
    val desc: String
)

@Composable
fun <T> RatioGroups(
    items: List<RatioOption<T>>,
    value: T,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.selectableGroup()) {
        items.forEach {
            val selected = (value == it.value)
            RatioItem(
                selected,
                { onClick(it.value) },
                it.title,
                it.desc
            )
        }
    }
}