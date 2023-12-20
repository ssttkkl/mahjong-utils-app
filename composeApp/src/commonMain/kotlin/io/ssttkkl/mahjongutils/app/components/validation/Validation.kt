package io.ssttkkl.mahjongutils.app.components.validation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ValidationField(
    errMsg: String?,
    modifier: Modifier = Modifier,
    content: @Composable (isError: Boolean) -> Unit
) {
    val isErr = errMsg != null
    Column(modifier) {
        content(isErr)
        AnimatedVisibility(isErr) {
            Text(
                errMsg ?: "",
                style = MaterialTheme.typography.bodySmall.copy(
                    MaterialTheme.colorScheme.error
                )
            )
        }
    }
}