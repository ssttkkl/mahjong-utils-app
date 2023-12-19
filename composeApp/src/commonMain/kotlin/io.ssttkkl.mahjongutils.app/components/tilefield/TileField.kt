package io.ssttkkl.mahjongutils.app.components.tilefield

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import io.ssttkkl.mahjongutils.app.components.tileime.UseTileIme
import io.ssttkkl.mahjongutils.app.utils.emoji
import io.ssttkkl.mahjongutils.app.utils.emojiToTile
import mahjongutils.models.Tile
import kotlin.math.max

val TilesTextStyle = TextStyle.Default.copy(
    fontWeight = FontWeight.Normal,
    fontSize = 30.sp,
    letterSpacing = 0.sp
)

@Composable
fun TileField(
    value: List<Tile>,
    onValueChange: (List<Tile>) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    textStyle: TextStyle = TilesTextStyle,
    isError: Boolean = false,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    val tilesText = value.joinToString("") { it.emoji }
    if (tilesText != textFieldValue.text) {
        // 保证选择的边界是两个emoji之间
        textFieldValue = TextFieldValue(tilesText)
    }

    UseTileIme(
        transformTile = {
            buildAnnotatedString {
                append(it.emoji)
            }
        }
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { raw ->
                // 保证选择的边界在两个emoji之间（每个emoji占两个字符）
                textFieldValue = raw.let {
                    it.copy(
                        selection = TextRange(
                            max(0, it.selection.start - it.selection.start % 2),
                            max(0, it.selection.end - it.selection.end % 2)
                        )
                    )
                }

                val tiles = buildList {
                    repeat(textFieldValue.text.length / 2) {
                        // 每个tile emoji占两个字符
                        add(emojiToTile(textFieldValue.text.substring(it * 2, it * 2 + 2)))
                    }
                }
                onValueChange(tiles)
            },
            modifier = modifier,
            textStyle = textStyle,
            label = {
                label?.let {
                    Text(it)
                }
            },
            trailingIcon = {
                Text(
                    stringResource(MR.strings.text_tiles_num_short, value.size),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            isError = isError
        )
    }
}
