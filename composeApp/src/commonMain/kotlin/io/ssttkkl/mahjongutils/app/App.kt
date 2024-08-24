package io.ssttkkl.mahjongutils.app

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ssttkkl.mahjongutils.app.components.appscaffold.AppScaffold
import io.ssttkkl.mahjongutils.app.components.appscaffold.LocalAppState
import io.ssttkkl.mahjongutils.app.components.appscaffold.UrlNavigationScreen
import io.ssttkkl.mahjongutils.app.screens.about.AboutScreen
import io.ssttkkl.mahjongutils.app.screens.about.OpenSourceLicensesScreen
import io.ssttkkl.mahjongutils.app.screens.furoshanten.FuroShantenScreen
import io.ssttkkl.mahjongutils.app.screens.hanhu.HanhuScreen
import io.ssttkkl.mahjongutils.app.screens.hora.HoraScreen
import io.ssttkkl.mahjongutils.app.screens.shanten.ShantenScreen
import io.ssttkkl.mahjongutils.app.theme.AppTheme
import kotlinx.coroutines.launch

private val screenRegistry: Map<String, () -> UrlNavigationScreen<*>> = buildMap {
    put(ShantenScreen.path) { ShantenScreen }
    put(FuroShantenScreen.path) { FuroShantenScreen }
    put(HoraScreen.path) { HoraScreen }
    put(HanhuScreen.path) { HanhuScreen }
    put(AboutScreen.path) { AboutScreen }
    put(OpenSourceLicensesScreen.path) { OpenSourceLicensesScreen }
}

private val navigatableScreens = listOf(
    ShantenScreen,
    FuroShantenScreen,
    HoraScreen,
    HanhuScreen,
    AboutScreen
).map { it.path }

@Composable
fun App(
    typography: Typography = MaterialTheme.typography,
    additionalContent: @Composable () -> Unit = {}
) {
    AppTheme(typography = typography) {
        AppScaffold(
            screenRegistry,
            navigatableScreens,
            additionalContent = additionalContent,
            navigationIcon = {
                val appState = LocalAppState.current
                val canGoBack = appState.navigator.voyager.canPop
                if (!canGoBack) {
                    if (appState.useNavigationDrawer) {
                        Icon(Icons.Default.Menu, "", Modifier.clickable {
                            appState.coroutineScope.launch {
                                if (appState.drawerState.isClosed) {
                                    appState.drawerState.open()
                                } else {
                                    appState.drawerState.close()
                                }
                            }
                        })
                    }
                } else {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, "", Modifier.clickable {
                        appState.navigator.voyager.pop()
                    })
                }
            }
        )
    }
}
