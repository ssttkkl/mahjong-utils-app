package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.Res
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen

data class FuroShantenResultScreen(
    val args: FuroChanceShantenArgs
) : NavigationScreen {
    override val title: String
        get() = Res.string.title_furo_shanten_result

    @Composable
    override fun Content() {
        Calculation(
            args,
            {
                args.calc()
            },
            onFailure = {
                PopAndShowMessageOnFailure(it)
            }
        ) {
            Text(it.toString())
        }
    }
}