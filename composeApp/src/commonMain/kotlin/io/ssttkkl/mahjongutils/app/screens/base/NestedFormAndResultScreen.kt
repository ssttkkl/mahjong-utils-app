package io.ssttkkl.mahjongutils.app.screens.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import io.ssttkkl.mahjongutils.app.utils.Spacing
import org.jetbrains.compose.resources.stringResource

@Stable
class NestedFormAndResultScreen<ARG, RES>(
    val formKey: String
) : NavigationScreen() {

    override val key: String
        get() = "${formKey}-formAndResult"

    private val formModel: NestedFormScreenModel<ARG, RES>
        @Composable
        get() = NestedFormScreen.rememberScreenModel(formKey)

    private val resultModel: NestedResultScreenModel<ARG, RES>
        @Composable
        get() = NestedResultScreen.rememberScreenModel(formKey)

    override val title: String
        @Composable
        get() = formModel.title?.let { stringResource(it) } ?: ""

    @Composable
    override fun ScreenContent() {
        val resultModel = resultModel

        with(Spacing.current) {
            Row {
                Box(Modifier.weight(2f)) {
                    formModel.formContent()
                }
                Spacer(Modifier.width(panesHorizontalSpacing))
                Box(Modifier.weight(3f)) {
                    NestedResultCalculation(resultModel)
                }
            }
        }
    }

    @Composable
    override fun RowScope.TopBarActions() {
        NestedFormTopBarActions(formModel)
    }
}
