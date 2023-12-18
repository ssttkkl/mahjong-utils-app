package io.ssttkkl.mahjongutils.app.components.combobox

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ssttkkl.mahjongutils.app.components.checkbox.CheckboxWithText

data class ComboOption<T>(
    val text: String,
    val value: T,
    val enabled: Boolean = true
)

private val comboBoxExpandedShape = RoundedCornerShape(8.dp).copy(
    bottomEnd = CornerSize(0.dp),
    bottomStart = CornerSize(0.dp)
)

private val comboBoxNotExpandedShape = RoundedCornerShape(8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.ComboBoxTextField(
    text: String,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = Modifier.menuAnchor().then(modifier),
        textStyle = TextStyle.Default.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        ),
        readOnly = true,
        value = text,
        onValueChange = {},
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        shape = if (expanded) comboBoxExpandedShape
        else comboBoxNotExpandedShape,
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            focusedIndicatorColor = Transparent,
            unfocusedIndicatorColor = Transparent
        ),
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiComboBox(
    selected: Set<T>,
    onSelected: (T, Set<T>) -> Unit,
    options: List<ComboOption<T>>,
    modifier: Modifier = Modifier,
    produceDisplayText: ((Collection<ComboOption<T>>) -> String)? = null,
    closeOnClick: Boolean = false,
    showCheckbox: Boolean = true,
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
            options.filter { it.value in selected }.let {
                produceDisplayText?.invoke(it) ?: it.joinToString { it.text }
            },
            expanded,
            modifier
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = {
                        if (showCheckbox) {
                            CheckboxWithText(it.value in selected, null) { Text(it.text) }
                        } else {
                            Text(it.text)
                        }
                    },
                    onClick = {
                        onSelected(it.value, selected + it.value)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ComboBox(
    selected: T,
    onSelected: (T) -> Unit,
    options: List<ComboOption<T>>,
    modifier: Modifier = Modifier,
) {
    MultiComboBox(
        setOf(selected),
        { it, _ -> onSelected(it) },
        options,
        modifier,
        closeOnClick = true,
        showCheckbox = false
    )
}