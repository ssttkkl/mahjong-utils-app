package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.navigator.LocalNavigator
import io.ssttkkl.mahjongutils.app.BuildKonfig
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.components.appscaffold.NavigationScreen
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.title_about
import mahjongutils.composeapp.generated.resources.title_about_appversion
import mahjongutils.composeapp.generated.resources.title_about_opensource_licenses
import mahjongutils.composeapp.generated.resources.title_about_opensource_repo
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object AboutScreen : NavigationScreen() {
    override val title: StringResource
        get() = Res.string.title_about

    @Composable
    override fun Content() {
        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.current

        val verticalScrollState = rememberScrollState()

        ScrollBox(verticalScrollState) {
            Column(Modifier.verticalScroll(verticalScrollState)) {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.title_about_appversion)) },
                    supportingContent = { Text(BuildKonfig.VERSION_NAME) }
                )
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.title_about_opensource_repo)) },
                    supportingContent = { Text(BuildKonfig.OPENSOURCE_REPO) },
                    modifier = Modifier.clickable {
                        uriHandler.openUri(BuildKonfig.OPENSOURCE_REPO)
                    }
                )
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.title_about_opensource_licenses)) },
                    supportingContent = { Text(BuildKonfig.OPENSOURCE_LICENSE) },
                    modifier = Modifier.clickable {
                        navigator?.push(OpenSourceLicensesScreen)
                    }
                )
            }
        }
    }
}