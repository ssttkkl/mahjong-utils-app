package io.ssttkkl.mahjongutils.app.components.checkbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxWithText(
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    text: @Composable () -> Unit,
) {
    Row(
        modifier.run {
            if (onCheckedChange != null) {
                this.clickable { onCheckedChange(!isChecked) }
            } else {
                this
            }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            isChecked,
            null,
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource
        )
        Spacer(Modifier.width(4.dp))
        text()
    }
}