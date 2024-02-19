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
    get() = when (this.name) {
        Yakus.Tsumo.name -> MR.strings.label_yaku_tsumo
        Yakus.Pinhu.name -> MR.strings.label_yaku_pinhu
        Yakus.Tanyao.name -> MR.strings.label_yaku_tanyao
        Yakus.Ipe.name -> MR.strings.label_yaku_ipe
        Yakus.SelfWind.name -> MR.strings.label_yaku_self_wind
        Yakus.RoundWind.name -> MR.strings.label_yaku_round_wind
        Yakus.Chankan.name -> MR.strings.label_yaku_chankan
        Yakus.Haku.name -> MR.strings.label_yaku_haku
        Yakus.Hatsu.name -> MR.strings.label_yaku_hatsu
        Yakus.Chun.name -> MR.strings.label_yaku_chun
        Yakus.Sanshoku.name -> MR.strings.label_yaku_sanshoku
        Yakus.Ittsu.name -> MR.strings.label_yaku_ittsu
        Yakus.Chanta.name -> MR.strings.label_yaku_chanta
        Yakus.Chitoi.name -> MR.strings.label_yaku_chitoi
        Yakus.Toitoi.name -> MR.strings.label_yaku_toitoi
        Yakus.Sananko.name -> MR.strings.label_yaku_sananko
        Yakus.Honroto.name -> MR.strings.label_yaku_honroto
        Yakus.Sandoko.name -> MR.strings.label_yaku_sandoko
        Yakus.Sankantsu.name -> MR.strings.label_yaku_sankantsu
        Yakus.Shosangen.name -> MR.strings.label_yaku_shosangen
        Yakus.Honitsu.name -> MR.strings.label_yaku_honitsu
        Yakus.Junchan.name -> MR.strings.label_yaku_junchan
        Yakus.Ryanpe.name -> MR.strings.label_yaku_ryanpe
        Yakus.Chinitsu.name -> MR.strings.label_yaku_chinitsu
        Yakus.Kokushi.name -> MR.strings.label_yaku_kokushi
        Yakus.Suanko.name -> MR.strings.label_yaku_suanko
        Yakus.Daisangen.name -> MR.strings.label_yaku_daisangen
        Yakus.Tsuiso.name -> MR.strings.label_yaku_tsuiso
        Yakus.Shousushi.name -> MR.strings.label_yaku_shousushi
        Yakus.Lyuiso.name -> MR.strings.label_yaku_lyuiso
        Yakus.Chinroto.name -> MR.strings.label_yaku_chinroto
        Yakus.Sukantsu.name -> MR.strings.label_yaku_sukantsu
        Yakus.Churen.name -> MR.strings.label_yaku_churen
        Yakus.Daisushi.name -> MR.strings.label_yaku_daisushi
        Yakus.ChurenNineWaiting.name -> MR.strings.label_yaku_churenNineWaiting
        Yakus.SuankoTanki.name -> MR.strings.label_yaku_suankoTanki
        Yakus.KokushiThirteenWaiting.name -> MR.strings.label_yaku_kokushiThirteenWaiting
        Yakus.Tenhou.name -> MR.strings.label_yaku_tenhou
        Yakus.Chihou.name -> MR.strings.label_yaku_chihou
        Yakus.WRichi.name -> MR.strings.label_yaku_wrichi
        Yakus.Richi.name -> MR.strings.label_yaku_richi
        Yakus.Ippatsu.name -> MR.strings.label_yaku_ippatsu
        Yakus.Rinshan.name -> MR.strings.label_yaku_rinshan
        Yakus.Chankan.name -> MR.strings.label_yaku_chankan
        Yakus.Haitei.name -> MR.strings.label_yaku_haitei
        Yakus.Houtei.name -> MR.strings.label_yaku_houtei
        else -> error("unknown yaku: $this")
    }