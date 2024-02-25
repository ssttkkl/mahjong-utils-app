package io.ssttkkl.mahjongutils.app.screens.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
import org.jetbrains.compose.resources.stringResource

@Composable
fun TilesPanelHeader(
    editingState: MutableState<Boolean>,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(Modifier.height(24.dp)) {
        Text(
            stringResource(Res.string.label_tiles_in_hand),
            Modifier.align(Alignment.CenterVertically)
        )

        if (!editingState.value) {
            IconButton(
                { editingState.value = true },
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Outlined.Edit, "", tint = MaterialTheme.colorScheme.primary)
            }
        } else {
            IconButton(
                onSubmit,
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Outlined.Check, "", tint = MaterialTheme.colorScheme.primary)
            }

            IconButton(
                onCancel,
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Outlined.Clear, "", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}