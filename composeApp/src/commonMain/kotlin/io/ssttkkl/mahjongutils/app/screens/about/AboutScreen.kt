package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.BuildKonfig
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen

object AboutScreen : NavigationScreen() {
    override val title: StringResource
        get() = MR.strings.title_about

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val verticalScrollState = rememberScrollState()

        ScrollBox(verticalScrollState) {
            Column(Modifier.verticalScroll(verticalScrollState)) {
                ListItem(
                    headlineContent = { Text(stringResource(MR.strings.title_about_appversion)) },
                    supportingContent = { Text(BuildKonfig.VERSION_NAME) }
                )
                ListItem(
                    headlineContent = { Text(stringResource(MR.strings.title_about_opensource_licenses)) },
                    modifier = Modifier.clickable {
                        navigator?.push(OpenSourceLicensesScreen)
                    }
                )
            }
        }
    }
}