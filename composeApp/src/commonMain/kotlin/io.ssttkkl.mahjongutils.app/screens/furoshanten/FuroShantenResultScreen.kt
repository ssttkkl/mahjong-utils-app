package io.ssttkkl.mahjongutils.app.screens.furoshanten

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ssttkkl.mahjongutils.app.components.calculation.Calculation
import io.ssttkkl.mahjongutils.app.components.calculation.PopAndShowMessageOnFailure

@Composable
fun FuroShantenResultScreen(args: FuroChanceShantenArgs) {
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