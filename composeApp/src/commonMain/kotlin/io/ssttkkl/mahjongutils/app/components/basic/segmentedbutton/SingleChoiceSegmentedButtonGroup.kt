package io.ssttkkl.mahjongutils.app.components.basic.segmentedbutton

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class SegmentedButtonOption<T>(
    val title: String,
    val value: T,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SingleChoiceSegmentedButtonGroup(
    options: List<SegmentedButtonOption<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier) {
        options.forEachIndexed { index, opt ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onValueChange(opt.value) },
                selected = opt.value == value
            ) {
                Text(opt.title)
            }
        }
    }
}