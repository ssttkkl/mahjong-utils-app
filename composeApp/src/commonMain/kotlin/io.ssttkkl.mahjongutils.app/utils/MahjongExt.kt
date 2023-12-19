package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.ssttkkl.mahjongutils.app.MR
import mahjongutils.models.Tile
import mahjongutils.models.TileType
import mahjongutils.models.Wind
import mahjongutils.models.isWind
import mahjongutils.yaku.Yaku
import mahjongutils.yaku.Yakus

// https://en.wikipedia.org/wiki/Mahjong_Tiles_(Unicode_block)
private val tileToEmojiMapping = buildMap {
    Tile.all.forEach {
        this[it] = when (it.type) {
            TileType.M -> "\uD83C" + '\uDC06'.plus(it.realNum)
            TileType.P -> "\uD83C" + '\uDC18'.plus(it.realNum)
            TileType.S -> "\uD83C" + '\uDC0F'.plus(it.realNum)
            TileType.Z -> {
                if (it.isWind) {
                    "\uD83C" + '\uDBFF'.plus(it.realNum)
                } else if (it.num == 5) {
                    "\uD83C\uDC06"
                } else if (it.num == 6) {
                    "\uD83C\uDC05"
                } else {
                    "\uD83C\uDC04"
                }
            }
        }
    }
}

private val emojiToTileMapping =
    tileToEmojiMapping.toList().associate { (tile, emoji) -> emoji to tile }

val Tile.emoji: String
    get() = tileToEmojiMapping[this]!!

fun emojiToTile(emoji: String): Tile {
    return emojiToTileMapping[emoji]
        ?: throw IllegalArgumentException("$emoji is not a tile emoji")
}

private val tileToImgResMapping = buildMap {
    this[Tile["1m"]] = MR.images.tile_1m
    this[Tile["2m"]] = MR.images.tile_2m
    this[Tile["3m"]] = MR.images.tile_3m
    this[Tile["4m"]] = MR.images.tile_4m
    this[Tile["5m"]] = MR.images.tile_5m
    this[Tile["6m"]] = MR.images.tile_6m
    this[Tile["7m"]] = MR.images.tile_7m
    this[Tile["8m"]] = MR.images.tile_8m
    this[Tile["9m"]] = MR.images.tile_9m

    this[Tile["1p"]] = MR.images.tile_1p
    this[Tile["2p"]] = MR.images.tile_2p
    this[Tile["3p"]] = MR.images.tile_3p
    this[Tile["4p"]] = MR.images.tile_4p
    this[Tile["5p"]] = MR.images.tile_5p
    this[Tile["6p"]] = MR.images.tile_6p
    this[Tile["7p"]] = MR.images.tile_7p
    this[Tile["8p"]] = MR.images.tile_8p
    this[Tile["9p"]] = MR.images.tile_9p

    this[Tile["1s"]] = MR.images.tile_1s
    this[Tile["2s"]] = MR.images.tile_2s
    this[Tile["3s"]] = MR.images.tile_3s
    this[Tile["4s"]] = MR.images.tile_4s
    this[Tile["5s"]] = MR.images.tile_5s
    this[Tile["6s"]] = MR.images.tile_6s
    this[Tile["7s"]] = MR.images.tile_7s
    this[Tile["8s"]] = MR.images.tile_8s
    this[Tile["9s"]] = MR.images.tile_9s

    this[Tile["1z"]] = MR.images.tile_1z
    this[Tile["2z"]] = MR.images.tile_2z
    this[Tile["3z"]] = MR.images.tile_3z
    this[Tile["4z"]] = MR.images.tile_4z
    this[Tile["5z"]] = MR.images.tile_5z
    this[Tile["6z"]] = MR.images.tile_6z
    this[Tile["7z"]] = MR.images.tile_7z
}

val Tile.painterResource: Painter
    @Composable
    get() = painterResource(tileToImgResMapping[this] ?: MR.images.tile_back)

@Composable
fun shantenNumText(shantenNum: Int): String {
    return when (shantenNum) {
        -1 -> stringResource(MR.strings.text_hora)
        0 -> stringResource(MR.strings.text_tenpai)
        else -> stringResource(
            MR.strings.text_shanten_num, shantenNum
        )
    }
}

val Wind.localizedName
    get() = when (this) {
        Wind.East -> MR.strings.label_wind_east
        Wind.South -> MR.strings.label_wind_south
        Wind.West -> MR.strings.label_wind_west
        Wind.North -> MR.strings.label_wind_north
    }

val Yaku.localizedName
    get() = when (this) {
        Yakus.Tenhou -> MR.strings.label_yaku_tenhou
        Yakus.Chihou -> MR.strings.label_yaku_chihou
        Yakus.WRichi -> MR.strings.label_yaku_wrichi
        Yakus.Richi -> MR.strings.label_yaku_richi
        Yakus.Ippatsu -> MR.strings.label_yaku_ippatsu
        Yakus.Rinshan -> MR.strings.label_yaku_rinshan
        Yakus.Chankan -> MR.strings.label_yaku_chankan
        Yakus.Haitei -> MR.strings.label_yaku_haitei
        Yakus.Houtei -> MR.strings.label_yaku_houtei
        else -> error("unknown yaku: $this")
    }