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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.screens.base.FormState
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_tiles_in_hand
import org.jetbrains.compose.resources.stringResource

@Composable
fun <F : FormState<ARG>, ARG> TilesPanelHeader(
    panelState: EditablePanelState<F, ARG>,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(Modifier.height(24.dp)) {
        Text(
            stringResource(Res.string.label_tiles_in_hand),
            Modifier.align(Alignment.CenterVertically)
        )

        if (!panelState.editing) {
            IconButton(
                {
                    panelState.editing = true;
                    panelState.form.fillFormWithArgs(panelState.originArgs)
                },
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Outlined.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
            }
        } else {
            IconButton(
                onSubmit,
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Outlined.Check, "Apply", tint = MaterialTheme.colorScheme.primary)
            }

            IconButton(
                onCancel,
                Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Outlined.Clear, "Cancel", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}