package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.screenModelScope
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppBottomSheetState
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowSnackbarOnFailure
import io.ssttkkl.mahjongutils.app.components.panel.LazyCardPanel
import io.ssttkkl.mahjongutils.app.components.panel.Panel
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.utils.Spacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class FormAndResultScreen<M : FormAndResultScreenModel<ARG, RES>, ARG, RES> :
    NavigationScreen() {
    companion object {
        fun isTwoPanes(windowSizeClass: WindowSizeClass): Boolean {
            return windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                    && windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact
        }
    }

    abstract val resultTitle: StringResource

    @Composable
    abstract fun getScreenModel(): M

    @Composable
    override fun RowScope.TopBarActions(appState: AppState) {
        val model = getScreenModel()
        with(Spacing.current) {
            IconButton(onClick = {
                appState.appBottomSheetState = AppBottomSheetState {
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
                    painterResource(MR.images.icon_history_outlined),
                    stringResource(MR.strings.label_history)
                )
            }

            IconButton(onClick = {
                model.resetForm()
            }) {
                Icon(Icons.Filled.Clear, stringResource(MR.strings.label_clear))
            }
        }
    }

    @Composable
    abstract fun FormContent(appState: AppState, model: M, modifier: Modifier)

    // 加上protected，ios link报错？？？
    @Composable
    /*protected*/ abstract fun HistoryItem(item: History<ARG>, model: M)

    protected abstract fun onClickHistoryItem(item: History<ARG>, model: M, appState: AppState)

    @Composable
    fun HistoryContent(
        model: M,
        modifier: Modifier,
        requestCloseModal: () -> Unit
    ) {
        val appState = LocalAppState.current
        val history by model.history.data.collectAsState(emptyList())

        @Composable
        fun PanelHeader() {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(MR.strings.label_history))
                TextButton(
                    onClick = {
                        model.screenModelScope.launch {
                            model.history.clear()
                        }
                    },
                    enabled = history.isNotEmpty()
                ) {
                    Text(stringResource(MR.strings.label_clear))
                }
            }
        }

        with(Spacing.current) {
            LazyColumn(modifier) {
                if (history.isEmpty()) {
                    item {
                        Panel(header = { PanelHeader() }) {
                            Text(
                                stringResource(MR.strings.text_empty_history),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                } else {
                    LazyCardPanel(
                        items = sequence { yieldAll(history) },
                        keyMapping = { "${it.createTime.toEpochMilliseconds()}-${it.args.hashCode()}" },
                        header = { PanelHeader() },
                        cardModifier = {
                            Modifier.clickable {
                                onClickHistoryItem(it, model, appState)
                                requestCloseModal()
                            }
                        },
                        content = { HistoryItem(it, model) }
                    )
                }

                item {
                    VerticalSpacerBetweenPanels()
                }
            }
        }
    }

    @Composable
    abstract fun ResultContent(appState: AppState, result: RES, modifier: Modifier)

    @Composable
    override fun Content() {
        super.Content()

        val appState = LocalAppState.current
        val model = getScreenModel()

        if (isTwoPanes(appState.windowSizeClass)) {
            TwoPaneContent(appState, model)
        } else {
            OnePaneContent(appState, model)
        }
    }

    @Composable
    private fun OnePaneContent(appState: AppState, model: M) {
        val coroutineScope = rememberCoroutineScope()
        val resultScreenModel = ResultScreen.rememberScreenModel(key)

        DisposableEffect(appState, model, coroutineScope) {
            val job = coroutineScope.launch {
                model.result.collectLatest { result ->
                    result?.let { result ->
                        // 将result移动至ResultScreen
                        // 保证从ResultScreen返回后不会重复跳转
                        // 除非调用了onResultMove将result再次移动回来（发生在旋转后由单栏变成双栏的场合）
                        resultScreenModel.apply {
                            title = resultTitle
                            resultHolder = ResultHolder(result) { model.result.value = it }
                            resultContent = {
                                ResultContent(appState, it as RES, Modifier)
                            }
                        }
                        appState.navigator.push(ResultScreen(key))
                        model.result.value = null
                    }
                }
            }

            onDispose {
                job.cancel()
            }
        }

        FormContent(appState, model, Modifier)
    }

    @Composable
    private fun TwoPaneContent(appState: AppState, model: M) {
        val result by model.result.collectAsState()

        with(Spacing.current) {
            Row {
                FormContent(appState, model, Modifier.weight(2f))
                Spacer(Modifier.width(panesHorizontalSpacing))
                Box(Modifier.weight(3f)) {
                    result?.let { result ->
                        Calculation(
                            result,
                            {
                                result.await()
                            },
                            onFailure = {
                                PopAndShowSnackbarOnFailure(it)
                            }
                        ) {
                            ResultContent(appState, it, Modifier)
                        }
                    }
                }
            }
        }
    }
}