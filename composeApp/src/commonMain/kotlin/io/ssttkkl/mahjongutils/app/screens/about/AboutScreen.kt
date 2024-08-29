package io.ssttkkl.mahjongutils.app.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import io.ssttkkl.mahjongutils.app.BuildKonfig
import io.ssttkkl.mahjongutils.app.components.appscaffold.NoParamUrlNavigationScreen
import io.ssttkkl.mahjongutils.app.components.scrollbox.ScrollBox
import io.ssttkkl.mahjongutils.app.utils.image.SavePhotoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.title_about
import mahjongutils.composeapp.generated.resources.title_about_appversion
import mahjongutils.composeapp.generated.resources.title_about_opensource_licenses
import mahjongutils.composeapp.generated.resources.title_about_opensource_repo
import okio.ByteString.Companion.decodeHex
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

object AboutScreen : NoParamUrlNavigationScreen() {
    override val path: String
        get() = "about"

    override val title: String
        @Composable
        get() = stringResource(Res.string.title_about)

    @OptIn(
        ExperimentalComposeUiApi::class, ExperimentalComposeApi::class,
        ExperimentalStdlibApi::class
    )
    @Composable
    override fun ScreenContent() {
        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.current

        val verticalScrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val captureController = rememberCaptureController()

        ScrollBox(verticalScrollState) {
            Column(Modifier.verticalScroll(verticalScrollState)) {
                var imgHex by remember { mutableStateOf("") }
                var height by remember { mutableStateOf("") }
                var width by remember { mutableStateOf("") }
                var img by remember { mutableStateOf<ImageBitmap?>(null) }

                TextField(imgHex, { imgHex = it }, maxLines = 1, label = { Text("imgHex") })
                TextField(
                    height,
                    { height = it },
                    label = { Text("height") })
                TextField(
                    width,
                    { width = it },
                    label = { Text("width") })

                Button({
                    coroutineScope.launch {
                        val capture = captureController.captureAsync(ColorType.RGBA_8888).await()
                        imgHex = capture.asSkiaBitmap().readPixels(
                            ImageInfo(
                                capture.width,
                                capture.height,
                                ColorType.RGBA_8888,
                                ColorAlphaType.UNPREMUL
                            )
                        )?.toHexString() ?: ""
                        height = capture.height.toString()
                        width = capture.width.toString()
                    }
                }, Modifier.capturable(captureController)) {
                    Text("capture")
                }

                Button({
                    coroutineScope.launch(Dispatchers.Default) {
                        try {
                            img = Image.makeRaster(
                                ImageInfo(
                                    width.toInt(),
                                    height.toInt(),
                                    ColorType.RGBA_8888,
                                    ColorAlphaType.UNPREMUL
                                ),
                                imgHex.decodeHex().toByteArray(),
                                width.toInt() * 4
                            ).toComposeImageBitmap()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }, Modifier.capturable(captureController)) {
                    Text("read")
                }


                Button({
                    coroutineScope.launch {
                        img?.let { SavePhotoUtils.save(it, "download") }
                    }
                }, Modifier.capturable(captureController)) {
                    Text("download")
                }

                img?.let {
                    Image(it, "null", modifier = Modifier.size(200.dp))
                }

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