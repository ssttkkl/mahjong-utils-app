package io.ssttkkl.mahjongutils.app.kuikly.components.icons

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import io.ssttkkl.mahjongutils.app.kuikly.Res
import io.ssttkkl.mahjongutils.app.kuikly.utils.painterResource

object Icons {

    object Default {
        @get:Composable
        val ArrowBack: Painter
            get() = painterResource(Res.images.icon_arrow_back)

        @get:Composable
        val Close: Painter
            get() = painterResource(Res.images.icon_close)

        @get:Composable
        val Menu: Painter
            get() = painterResource(Res.images.icon_menu)
    }
}