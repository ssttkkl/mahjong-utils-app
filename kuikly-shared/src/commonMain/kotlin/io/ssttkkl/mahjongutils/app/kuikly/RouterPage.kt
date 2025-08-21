package io.ssttkkl.mahjongutils.app.kuikly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.toComposeColor
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.utils.urlParams
import io.ssttkkl.mahjongutils.app.kuikly.base.BasePager
import io.ssttkkl.mahjongutils.app.kuikly.base.bridgeModule
import io.ssttkkl.mahjongutils.app.kuikly.base.Utils
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth

@Page("router", supportInLocal = true)
internal class ComposeRoutePager : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            ComposeRouteImpl()
        }
    }

    companion object {
        const val PLACEHOLDER = "输入pageName（不区分大小写）"
        const val CACHE_KEY = "router_last_input_key2"
        const val LOGO = "https://vfiles.gtimg.cn/wuji_dashboard/wupload/xy/starter/62394e19.png"
        const val JUMP_TEXT = "跳转"
        const val TEXT_KEY = "text"
        const val TITLE = "Kuikly页面路由"
        const val AAR_MODE_TIP = "如：router 或者 router&key=value （&后面为页面参数）"
    }
}

@Composable
internal fun ComposeRouteImpl() {
    var textFieldValue by remember { mutableStateOf("") }
    val localPager = LocalActivity.current.getPager() as Pager
    val statusBarHeight = LocalActivity.current.pageData.statusBarHeight
    Column(
        modifier = Modifier.padding(top = (statusBarHeight + 15).dp).fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = ComposeRoutePager.TITLE, fontSize = 18.sp, color = Color(0xFF7B7FE4)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier.size(250.dp),
            painter = rememberAsyncImagePainter(ComposeRoutePager.LOGO),
            contentDescription = null,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.weight(1f).padding(horizontal = 12.dp).background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF7B7FE4), Color(0xFFA65CF9))
                    ), shape = RoundedCornerShape(5.dp)
                ).padding(2.dp)
            ) {
                Box(
                    modifier = Modifier.background(
                        color = Color.White, shape = RoundedCornerShape(5.dp)
                    ).padding(12.dp)
                ) {
                    TextField(
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = ComposeRoutePager.PLACEHOLDER,
                        placeholderColor = com.tencent.kuikly.core.base.Color(0xAA23D3FD)
                            .toComposeColor(),
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                    )
                }
            }
            Button(
                modifier = Modifier.padding(12.dp).background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF7B7FE4), Color(0xFFA65CF9))
                    ), shape = RoundedCornerShape(5.dp)
                ).padding(12.dp),
                onClick = {
                    if (textFieldValue.isEmpty()) {
                        Utils.bridgeModule(localPager.pagerId).toast("请输入PageName")
                    } else {
                        localPager.acquireModule<SharedPreferencesModule>(
                            SharedPreferencesModule.MODULE_NAME
                        ).setItem(
                            ComposeRoutePager.CACHE_KEY, textFieldValue
                        )
                        jumpPage(localPager, textFieldValue)
                    }
                },
            ) {
                Text(ComposeRoutePager.JUMP_TEXT, color = Color.White)
            }
        }

        Text(
            text = ComposeRoutePager.AAR_MODE_TIP, fontSize = 12.sp, color = Color(0xFF7B7FE4)
        )
    }
}

fun jumpPage(pager: Pager, inputText: String) {
    val params = urlParams("pageName=$inputText")
    val pageData = JSONObject()
    params.forEach {
        pageData.put(it.key, it.value)
    }
    val pageName = pageData.optString("pageName")
    pager.acquireModule<RouterModule>(RouterModule.MODULE_NAME).openPage(pageName, pageData)
}