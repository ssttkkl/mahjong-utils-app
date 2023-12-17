package io.ssttkkl.mahjongutils.app.components.ratio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

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