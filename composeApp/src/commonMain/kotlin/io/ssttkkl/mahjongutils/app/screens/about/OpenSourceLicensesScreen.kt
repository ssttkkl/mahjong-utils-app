package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.readTextAsState
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.screens.base.NavigationScreen

object OpenSourceLicensesScreen : NavigationScreen() {
    override val title: StringResource?
        get() = MR.strings.title_about_opensource_licenses

    @Composable
    override fun Content() {
        val aboutlibraries by MR.files.aboutlibraries.readTextAsState()
        LibrariesContainer(
            aboutlibraries ?: "",
            Modifier.fillMaxSize()
        )
    }
}