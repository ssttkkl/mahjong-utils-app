package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.shreyaspatil.capturable.controller.CaptureController
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreenModel
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import kotlinx.coroutines.Deferred
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Stable
class NestedResultScreen<ARG, RES>(
    val resultKey: String,
) : NavigationScreen() {

    companion object {
        @Composable
        fun <ARG, RES> rememberScreenModel(
            resultKey: String,
            navigator: Navigator = LocalNavigator.currentOrThrow
        ): NestedResultScreenModel<ARG, RES> {
            // 统一存在子级Navigator中
            val model = navigator.rememberNavigatorScreenModel("${resultKey}-result") {
                NestedResultScreenModel<ARG, RES>()
            }
            return model
        }
    }

    override val key: String
        get() = "${resultKey}-result"

    private val model: NestedResultScreenModel<ARG, RES>
        @Composable
        get() = rememberScreenModel(resultKey)


    override val title: String
        @Composable
        get() = model.title?.let { stringResource(it) } ?: ""

    @Composable
    override fun ScreenContent() {
        val model = model
        NestedResultCalculation(model)
    }

    @Composable
    override fun RowScope.TopBarActions() {
        NestedResultTopBarActions(model)
    }
}

@Stable
class NestedResultScreenModel<ARG, RES> : UrlNavigationScreenModel() {
    var title by mutableStateOf<StringResource?>(null)

    var parentScreenModel by mutableStateOf<FormAndResultScreenModel<ARG, RES>?>(null)

    var result by mutableStateOf<Deferred<RES>?>(null)

    var resultContent by mutableStateOf<@Composable (RES) -> Unit>({
        LoggerFactory.getLogger(this::class).debug("no resultContent")
    })
}