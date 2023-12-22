package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
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
        Yakus.Tsumo -> MR.strings.label_yaku_tsumo
        Yakus.Pinhu -> MR.strings.label_yaku_pinhu
        Yakus.Tanyao -> MR.strings.label_yaku_tanyao
        Yakus.Ipe -> MR.strings.label_yaku_ipe
        Yakus.SelfWind -> MR.strings.label_yaku_self_wind
        Yakus.RoundWind -> MR.strings.label_yaku_round_wind
        Yakus.Chankan -> MR.strings.label_yaku_chankan
        Yakus.Haku -> MR.strings.label_yaku_haku
        Yakus.Hatsu -> MR.strings.label_yaku_hatsu
        Yakus.Chun -> MR.strings.label_yaku_chun
        Yakus.Sanshoku -> MR.strings.label_yaku_sanshoku
        Yakus.Ittsu -> MR.strings.label_yaku_ittsu
        Yakus.Chanta -> MR.strings.label_yaku_chanta
        Yakus.Chitoi -> MR.strings.label_yaku_chitoi
        Yakus.Toitoi -> MR.strings.label_yaku_toitoi
        Yakus.Sananko -> MR.strings.label_yaku_sananko
        Yakus.Honroto -> MR.strings.label_yaku_honroto
        Yakus.Sandoko -> MR.strings.label_yaku_sandoko
        Yakus.Sankantsu -> MR.strings.label_yaku_sankantsu
        Yakus.Shosangen -> MR.strings.label_yaku_shosangen
        Yakus.Honitsu -> MR.strings.label_yaku_honitsu
        Yakus.Junchan -> MR.strings.label_yaku_junchan
        Yakus.Ryanpe -> MR.strings.label_yaku_ryanpe
        Yakus.Chinitsu -> MR.strings.label_yaku_chinitsu
        Yakus.Kokushi -> MR.strings.label_yaku_kokushi
        Yakus.Suanko -> MR.strings.label_yaku_suanko
        Yakus.Daisangen -> MR.strings.label_yaku_daisangen
        Yakus.Tsuiso -> MR.strings.label_yaku_tsuiso
        Yakus.Shousushi -> MR.strings.label_yaku_shousushi
        Yakus.Lyuiso -> MR.strings.label_yaku_lyuiso
        Yakus.Chinroto -> MR.strings.label_yaku_chinroto
        Yakus.Sukantsu -> MR.strings.label_yaku_sukantsu
        Yakus.Churen -> MR.strings.label_yaku_churen
        Yakus.Daisushi -> MR.strings.label_yaku_daisushi
        Yakus.ChurenNineWaiting -> MR.strings.label_yaku_churenNineWaiting
        Yakus.SuankoTanki -> MR.strings.label_yaku_suankoTanki
        Yakus.KokushiThirteenWaiting -> MR.strings.label_yaku_kokushiThirteenWaiting
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