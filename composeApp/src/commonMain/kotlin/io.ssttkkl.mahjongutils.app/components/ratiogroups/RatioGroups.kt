package io.ssttkkl.mahjongutils.app.components.ratiogroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.Spacing

data class RatioOption<T>(
    val value: T,
    val title: String,
    val desc: String
)

@Composable
fun <T> RatioGroups(
    items: List<RatioOption<T>>,
    value: T,
    onValueChanged: (T) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        items.forEach {
            val selected = (value == it.value)
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = selected,
                        onClick = { onValueChanged(it.value) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = Spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected,
                    onClick = null
                )
                Column {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = it.desc,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
    }
}