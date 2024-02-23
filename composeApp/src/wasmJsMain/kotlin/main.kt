import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.CanvasBasedWindow
import io.ssttkkl.mahjongutils.app.App
import mahjongutils.composeapp.generated.resources.Res

suspend fun loadCjkFont(): FontFamily {
    val regular = Res.readBytes("font/NotoSansSC-Regular.ttf")

    return FontFamily(
        Font(identity = "CJKRegular", data = regular, weight = FontWeight.Normal),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        var typography by remember { mutableStateOf<Typography?>(null) }
        LaunchedEffect(Unit) {
            val fontFamily = loadCjkFont()
            val defaultTypography = Typography()
            typography = Typography(
                displayLarge = defaultTypography.displayLarge.copy(fontFamily = fontFamily),
                displayMedium = defaultTypography.displayMedium.copy(fontFamily = fontFamily),
                displaySmall = defaultTypography.displaySmall.copy(fontFamily = fontFamily),

                headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = fontFamily),
                headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = fontFamily),
                headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = fontFamily),

                titleLarge = defaultTypography.titleLarge.copy(fontFamily = fontFamily),
                titleMedium = defaultTypography.titleMedium.copy(fontFamily = fontFamily),
                titleSmall = defaultTypography.titleSmall.copy(fontFamily = fontFamily),

                bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = fontFamily),
                bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = fontFamily),
                bodySmall = defaultTypography.bodySmall.copy(fontFamily = fontFamily),

                labelLarge = defaultTypography.labelLarge.copy(fontFamily = fontFamily),
                labelMedium = defaultTypography.labelMedium.copy(fontFamily = fontFamily),
                labelSmall = defaultTypography.labelSmall.copy(fontFamily = fontFamily)
            )
        }
        App(typography = typography ?: MaterialTheme.typography)
    }
}