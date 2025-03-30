package io.ssttkkl.mahjongutils.app.base.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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

@Composable
fun ValidationField(
    errMsg: StringResource?,
    modifier: Modifier = Modifier,
    content: @Composable (isError: Boolean) -> Unit
) {
    ValidationField(
        errMsg?.let { stringResource(errMsg) },
        modifier, content
    )
}


@Composable
fun ValidationField(
    errMsg: List<StringResource>,
    modifier: Modifier = Modifier,
    content: @Composable (isError: Boolean) -> Unit
) {
    ValidationField(
        if (errMsg.isEmpty())
            null
        else
            @Suppress("SimplifiableCallChain")
            errMsg.map { stringResource(it) }
                .joinToString("\n"),
        modifier, content
    )
}