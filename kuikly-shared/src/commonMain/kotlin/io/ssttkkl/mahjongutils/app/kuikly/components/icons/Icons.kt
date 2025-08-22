package io.ssttkkl.mahjongutils.app.kuikly.components.icons

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.ui.graphics.painter.Painter
import com.tencent.kuikly.core.base.attr.ImageUri

@OptIn(InternalResourceApi::class)
object Icons {
    @Composable
    private fun rememberPainterAsset(path: String): Painter {
        return painterResource(DrawableResource(ImageUri.commonAssets(path).toUrl("")))
    }

    object Default {
        @get:Composable
        val ArrowBack: Painter
            get() = rememberPainterAsset("light_icons/arrow_back.png")

        @get:Composable
        val Close: Painter
            get() = rememberPainterAsset("light_icons/close.png")

        @get:Composable
        val Menu: Painter
            get() = rememberPainterAsset("light_icons/menu.png")
    }
}