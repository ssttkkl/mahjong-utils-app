package io.ssttkkl.mahjongutils.app.kuikly.components

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.pager.Pager

@Composable
fun CurrentPager(): Pager {
    return LocalActivity.current.getPager() as Pager
}

fun Pager.router(): RouterModule {
    return acquireModule<RouterModule>(RouterModule.MODULE_NAME)
}