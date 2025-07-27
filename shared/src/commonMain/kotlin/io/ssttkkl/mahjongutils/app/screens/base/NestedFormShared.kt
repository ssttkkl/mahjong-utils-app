package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.core.model.screenModelScope
import io.ssttkkl.mahjongutils.app.base.Spacing
import io.ssttkkl.mahjongutils.app.base.components.LazyCardPanel
import io.ssttkkl.mahjongutils.app.base.components.Panel
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppBottomSheetState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.icon_history_outlined
import mahjongutils.composeapp.generated.resources.label_clear
import mahjongutils.composeapp.generated.resources.label_history
import mahjongutils.composeapp.generated.resources.text_empty_history
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun <ARG, RES> NestedFormTopBarActions(model: NestedFormScreenModel<ARG, RES>) {
    val appState = LocalAppState.current
    val density = LocalDensity.current
    with(Spacing.current) {
        if (model.parentScreenModel?.history != null) {
            IconButton(onClick = {
                appState.appBottomSheetState = AppBottomSheetState(density) {
                    HistoryContent(
                        model,
                        Modifier.windowHorizontalMargin(),
                        requestCloseModal = {
                            appState.appBottomSheetState.visible = false
                        }
                    )
                }
                appState.appBottomSheetState.visible = true
            }) {
                Icon(
                    painterResource(Res.drawable.icon_history_outlined),
                    stringResource(Res.string.label_history)
                )
            }
        }

        IconButton(onClick = {
            model.parentScreenModel?.resetForm()
        }) {
            Icon(Icons.Filled.Clear, stringResource(Res.string.label_clear))
        }
    }
}

@Composable
private fun <ARG, RES> HistoryContent(
    model: NestedFormScreenModel<ARG, RES>,
    modifier: Modifier,
    requestCloseModal: () -> Unit
) {
    val model = model
    val parentModel = model.parentScreenModel
    val historyState = parentModel?.history?.data?.collectAsState(emptyList())

    @Composable
    fun PanelHeader() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.label_history))
            TextButton(
                onClick = {
                    parentModel?.screenModelScope?.launch {
                        parentModel.history?.clear()
                    }
                },
                enabled = !historyState?.value.isNullOrEmpty()
            ) {
                Text(stringResource(Res.string.label_clear))
            }
        }
    }

    with(Spacing.current) {
        LazyColumn(modifier) {
            if (historyState?.value.isNullOrEmpty()) {
                item {
                    Panel(header = { PanelHeader() }) {
                        Text(
                            stringResource(Res.string.text_empty_history),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            } else {
                LazyCardPanel(
                    items = sequence { historyState?.value?.let { yieldAll(it) } },
                    keyMapping = { "${it.createTime.toEpochMilliseconds()}-${it.args.hashCode()}" },
                    header = { PanelHeader() },
                    cardModifier = {
                        Modifier.clickable {
                            model.onClickHistoryItem(it)
                            requestCloseModal()
                        }
                    },
                    content = { model.historyItem(it) }
                )
            }

            item {
                VerticalSpacerBetweenPanels()
            }
        }
    }
}