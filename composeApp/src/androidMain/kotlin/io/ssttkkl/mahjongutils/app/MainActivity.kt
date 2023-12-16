package io.ssttkkl.mahjongutils.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }

        val systemLocale = resources.configuration.locales.get(0)
        Toast.makeText(this, systemLocale.toLanguageTag(), Toast.LENGTH_LONG)
            .show()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}