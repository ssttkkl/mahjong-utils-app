package io.ssttkkl.mahjongutils.app.components.validation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ValidationField(
    errMsg: String?,
    content: @Composable (isError: Boolean) -> Unit
) {
    val isErr = errMsg != null
    Column {
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