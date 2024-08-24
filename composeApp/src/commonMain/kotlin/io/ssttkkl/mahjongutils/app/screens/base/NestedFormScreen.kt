package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.models.base.History
import io.ssttkkl.mahjongutils.app.utils.log.LoggerFactory
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class NestedFormScreen<ARG, RES>(
    val formKey: String
) : NavigationScreen() {

    companion object {
        @Composable
        fun <ARG, RES> rememberScreenModel(
            formKey: String,
            navigator: Navigator = checkNotNull(LocalNavigator.current)
        ): NestedFormScreenModel<ARG, RES> {
            val model = navigator.rememberNavigatorScreenModel("${formKey}-form") {
                NestedFormScreenModel<ARG, RES>()
            }
            return model
        }
    }

    override val key: String
        get() = "${formKey}-form"

    private val model: NestedFormScreenModel<ARG, RES>
        @Composable
        get() = rememberScreenModel(formKey)

    override val title: String
        @Composable
        get() {
            return model.title?.let { stringResource(it) } ?: ""
        }

    @Composable
    override fun ScreenContent() {
        model.formContent()
    }

    @Composable
    override fun RowScope.TopBarActions() {
        val model = model
        val shared = remember(model) { NestedFormShared(model) }
        shared.TopBarActions()
    }
}

class NestedFormScreenModel<ARG, RES> : ScreenModel {
    var title by mutableStateOf<StringResource?>(null)

    var parentScreenModel by mutableStateOf<FormAndResultScreenModel<ARG, RES>?>(null)

    var formContent by mutableStateOf<@Composable () -> Unit>({
        LoggerFactory.getLogger(this::class).debug("no formContent")
    })
    var historyItem by mutableStateOf<@Composable (History<ARG>) -> Unit>({
        error("not specified")
    })
    var onClickHistoryItem by mutableStateOf<(History<ARG>) -> Unit>({
        error("not specified")
    })
}