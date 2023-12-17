package io.ssttkkl.mahjongutils.app.components.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.ssttkkl.mahjongutils.app.utils.Spacing

data class TableColumn<T>(
    val title: String,
    val weight: Float,
    val content: @Composable (record: T, index: Int) -> Unit
)

@Composable
fun <T> Table(
    columns: List<TableColumn<T>>,
    data: List<T>,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
            columns.forEach { col ->
                Text(
                    col.title,
                    modifier = Modifier.weight(col.weight)
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Divider()

        data.forEachIndexed { index, item ->
            Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
                columns.forEach { col ->
                    Box(
                        Modifier.weight(col.weight)
                            .padding(8.dp)
                    ) {
                        col.content(item, index)
                    }
                }
            }

            if (index != data.size - 1) {
                Divider()
            }
        }
    }
}