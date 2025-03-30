package io.ssttkkl.mahjongutils.app.base.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Immutable
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


@Composable
fun RatioItem(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    desc: String?
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = if (!desc.isNullOrEmpty()) ({ Text(desc) }) else null,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onClick() },
                role = Role.RadioButton
            ),
        leadingContent = {
            RadioButton(
                selected = selected,
                onClick = null
            )
        }
    )
}