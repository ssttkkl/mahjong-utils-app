package io.ssttkkl.mahjongutils.app.base.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Composable
fun SwitchItem(
    selected: Boolean,
    onSwitched: (Boolean) -> Unit,
    title: String,
    desc: String? = null,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = if (!desc.isNullOrEmpty()) ({ Text(desc) }) else null,
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onSwitched(!selected) },
                role = Role.RadioButton
            ),
        trailingContent = {
            Switch(
                checked = selected,
                onCheckedChange = null
            )
        },
        colors = colors
    )
}