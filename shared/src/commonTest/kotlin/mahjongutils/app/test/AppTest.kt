package mahjongutils.app.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

class AppTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testExample() = runComposeUiTest {
        setContent {
            TestApp()
        }
    }
}

@Composable
expect fun TestApp()