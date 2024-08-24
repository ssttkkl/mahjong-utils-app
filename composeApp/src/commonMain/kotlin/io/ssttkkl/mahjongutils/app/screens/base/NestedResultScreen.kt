package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreenModel
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class NestedResultScreen<ARG, RES>(
    val resultKey: String,
) : NavigationScreen() {

    companion object {
        @Composable
        fun <ARG, RES> rememberScreenModel(
            resultKey: String,
            navigator: Navigator = checkNotNull(LocalNavigator.current)
        ): NestedResultScreenModel<ARG, RES> {
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
        val shared = remember(model) { NestedResultShared(model) }
        shared.ResultCalculation()
    }
}

class NestedResultScreenModel<ARG, RES> : UrlNavigationScreenModel() {
    var title by mutableStateOf<StringResource?>(null)

    var parentScreenModel by mutableStateOf<FormAndResultScreenModel<ARG, RES>?>(null)

    var resultContent by mutableStateOf<@Composable (RES) -> Unit>({
        LoggerFactory.getLogger(this::class).debug("no resultContent")
    })
}