package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import io.github.skeptick.libres.compose.painterResource
import io.ssttkkl.mahjongutils.app.Res
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
    this[Tile["1m"]] = Res.image.tile_1m
    this[Tile["2m"]] = Res.image.tile_2m
    this[Tile["3m"]] = Res.image.tile_3m
    this[Tile["4m"]] = Res.image.tile_4m
    this[Tile["5m"]] = Res.image.tile_5m
    this[Tile["6m"]] = Res.image.tile_6m
    this[Tile["7m"]] = Res.image.tile_7m
    this[Tile["8m"]] = Res.image.tile_8m
    this[Tile["9m"]] = Res.image.tile_9m

    this[Tile["1p"]] = Res.image.tile_1p
    this[Tile["2p"]] = Res.image.tile_2p
    this[Tile["3p"]] = Res.image.tile_3p
    this[Tile["4p"]] = Res.image.tile_4p
    this[Tile["5p"]] = Res.image.tile_5p
    this[Tile["6p"]] = Res.image.tile_6p
    this[Tile["7p"]] = Res.image.tile_7p
    this[Tile["8p"]] = Res.image.tile_8p
    this[Tile["9p"]] = Res.image.tile_9p

    this[Tile["1s"]] = Res.image.tile_1s
    this[Tile["2s"]] = Res.image.tile_2s
    this[Tile["3s"]] = Res.image.tile_3s
    this[Tile["4s"]] = Res.image.tile_4s
    this[Tile["5s"]] = Res.image.tile_5s
    this[Tile["6s"]] = Res.image.tile_6s
    this[Tile["7s"]] = Res.image.tile_7s
    this[Tile["8s"]] = Res.image.tile_8s
    this[Tile["9s"]] = Res.image.tile_9s

    this[Tile["1z"]] = Res.image.tile_1z
    this[Tile["2z"]] = Res.image.tile_2z
    this[Tile["3z"]] = Res.image.tile_3z
    this[Tile["4z"]] = Res.image.tile_4z
    this[Tile["5z"]] = Res.image.tile_5z
    this[Tile["6z"]] = Res.image.tile_6z
    this[Tile["7z"]] = Res.image.tile_7z
}

val Tile.painterResource: Painter
    @Composable
    get() = (tileToImgResMapping[this] ?: Res.image.tile_back).painterResource()

fun shantenNumText(shantenNum: Int): String {
    return when (shantenNum) {
        -1 -> Res.string.text_hora
        0 -> Res.string.text_tenpai
        else -> Res.string.text_shanten_num.format(shantenNum)
    }
}

val Wind.localizedName
    get() = when (this) {
        Wind.East -> Res.string.label_wind_east
        Wind.South -> Res.string.label_wind_south
        Wind.West -> Res.string.label_wind_west
        Wind.North -> Res.string.label_wind_north
    }

val Yaku.localizedName
    get() = when (this) {
        Yakus.Tenhou -> Res.string.label_yaku_tenhou
        Yakus.Chihou -> Res.string.label_yaku_chihou
        Yakus.WRichi -> Res.string.label_yaku_wrichi
        Yakus.Richi -> Res.string.label_yaku_richi
        Yakus.Ippatsu -> Res.string.label_yaku_ippatsu
        Yakus.Rinshan -> Res.string.label_yaku_rinshan
        Yakus.Chankan -> Res.string.label_yaku_chankan
        Yakus.Haitei -> Res.string.label_yaku_haitei
        Yakus.Houtei -> Res.string.label_yaku_houtei
        else -> ""
    }