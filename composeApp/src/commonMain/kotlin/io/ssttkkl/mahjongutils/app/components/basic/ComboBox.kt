package io.ssttkkl.mahjongutils.app.components.basic

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class ComboOption<T>(
    val text: String,
    val value: T,
    val enabled: Boolean = true
)

sealed class ChooseAction<T> {
    data class OnChoose<T>(val value: T) : ChooseAction<T>()
    data class OnNotChoose<T>(val value: T) : ChooseAction<T>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.ComboBoxTextField(
    text: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        modifier = Modifier.menuAnchor().then(modifier),
        textStyle = TextStyle.Default.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        ),
        readOnly = true,
        value = text,
        onValueChange = {},
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (!expanded)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.primary,
        ),
        maxLines = 1,
        label = label,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiComboBox(
    chosen: Set<T>,
    onChosen: (ChooseAction<T>) -> Unit,
    options: List<ComboOption<T>>,
    modifier: Modifier = Modifier,
    produceDisplayText: (@Composable (Collection<ComboOption<T>>) -> String)? = null,
    closeOnClick: Boolean = false,
    showCheckbox: Boolean = true,
    label: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier,
    ) {
        ComboBoxTextField(
            options.filter { it.value in chosen }.let {
                produceDisplayText?.invoke(it) ?: it.joinToString { it.text }
            },
            expanded,
            modifier,
            label
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = {
                        if (showCheckbox) {
                            CheckboxWithText(it.value in chosen, null) { Text(it.text) }
                        } else {
                            Text(it.text)
                        }
                    },
                    onClick = {
                        if (it.value in chosen) {
                            onChosen(ChooseAction.OnNotChoose(it.value))
                        } else {
                            onChosen(ChooseAction.OnChoose(it.value))
                        }
                        if (closeOnClick) {
                            expanded = false
                        }
                    },
                    enabled = it.enabled,
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}


@Composable
fun <T> ComboBox(
    selected: T,
    onSelected: (T) -> Unit,
    options: List<ComboOption<T>>,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
) {
    MultiComboBox(
        setOf(selected),
        { (it as? ChooseAction.OnChoose<T>)?.let { onSelected(it.value) } },
        options,
        modifier,
        closeOnClick = true,
        showCheckbox = false,
        label = label
    )
}