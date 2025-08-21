package io.ssttkkl.mahjongutils.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.ssttkkl.mahjongutils.app.kuikly.KuiklyRenderActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Button({
                    startActivity(Intent(this@MainActivity, KuiklyRenderActivity::class.java))
                }) {
                    Text("Go to Kuikly")
                }

                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}