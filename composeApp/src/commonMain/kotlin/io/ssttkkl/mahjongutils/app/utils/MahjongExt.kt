package io.ssttkkl.mahjongutils.app.utils

import androidx.compose.runtime.Composable
import mahjongutils.composeapp.generated.resources.Res
import mahjongutils.composeapp.generated.resources.label_wind_east
import mahjongutils.composeapp.generated.resources.label_wind_north
import mahjongutils.composeapp.generated.resources.label_wind_south
import mahjongutils.composeapp.generated.resources.label_wind_west
import mahjongutils.composeapp.generated.resources.label_yaku_chankan
import mahjongutils.composeapp.generated.resources.label_yaku_chanta
import mahjongutils.composeapp.generated.resources.label_yaku_chihou
import mahjongutils.composeapp.generated.resources.label_yaku_chinitsu
import mahjongutils.composeapp.generated.resources.label_yaku_chinroto
import mahjongutils.composeapp.generated.resources.label_yaku_chitoi
import mahjongutils.composeapp.generated.resources.label_yaku_chun
import mahjongutils.composeapp.generated.resources.label_yaku_churen
import mahjongutils.composeapp.generated.resources.label_yaku_churenNineWaiting
import mahjongutils.composeapp.generated.resources.label_yaku_daisangen
import mahjongutils.composeapp.generated.resources.label_yaku_daisushi
import mahjongutils.composeapp.generated.resources.label_yaku_haitei
import mahjongutils.composeapp.generated.resources.label_yaku_haku
import mahjongutils.composeapp.generated.resources.label_yaku_hatsu
import mahjongutils.composeapp.generated.resources.label_yaku_honitsu
import mahjongutils.composeapp.generated.resources.label_yaku_honroto
import mahjongutils.composeapp.generated.resources.label_yaku_houtei
import mahjongutils.composeapp.generated.resources.label_yaku_ipe
import mahjongutils.composeapp.generated.resources.label_yaku_ippatsu
import mahjongutils.composeapp.generated.resources.label_yaku_ittsu
import mahjongutils.composeapp.generated.resources.label_yaku_junchan
import mahjongutils.composeapp.generated.resources.label_yaku_kokushi
import mahjongutils.composeapp.generated.resources.label_yaku_kokushiThirteenWaiting
import mahjongutils.composeapp.generated.resources.label_yaku_lyuiso
import mahjongutils.composeapp.generated.resources.label_yaku_pinhu
import mahjongutils.composeapp.generated.resources.label_yaku_richi
import mahjongutils.composeapp.generated.resources.label_yaku_rinshan
import mahjongutils.composeapp.generated.resources.label_yaku_round_wind
import mahjongutils.composeapp.generated.resources.label_yaku_ryanpe
import mahjongutils.composeapp.generated.resources.label_yaku_sananko
import mahjongutils.composeapp.generated.resources.label_yaku_sandoko
import mahjongutils.composeapp.generated.resources.label_yaku_sankantsu
import mahjongutils.composeapp.generated.resources.label_yaku_sanshoku
import mahjongutils.composeapp.generated.resources.label_yaku_self_wind
import mahjongutils.composeapp.generated.resources.label_yaku_shosangen
import mahjongutils.composeapp.generated.resources.label_yaku_shousushi
import mahjongutils.composeapp.generated.resources.label_yaku_suanko
import mahjongutils.composeapp.generated.resources.label_yaku_suankoTanki
import mahjongutils.composeapp.generated.resources.label_yaku_sukantsu
import mahjongutils.composeapp.generated.resources.label_yaku_tanyao
import mahjongutils.composeapp.generated.resources.label_yaku_tenhou
import mahjongutils.composeapp.generated.resources.label_yaku_toitoi
import mahjongutils.composeapp.generated.resources.label_yaku_tsuiso
import mahjongutils.composeapp.generated.resources.label_yaku_tsumo
import mahjongutils.composeapp.generated.resources.label_yaku_wrichi
import mahjongutils.composeapp.generated.resources.text_hora
import mahjongutils.composeapp.generated.resources.text_shanten_num
import mahjongutils.composeapp.generated.resources.text_tenpai
import mahjongutils.models.Tile
import mahjongutils.models.TileType
import mahjongutils.models.Wind
import mahjongutils.models.isWind
import mahjongutils.yaku.Yaku
import mahjongutils.yaku.Yakus
import org.jetbrains.compose.resources.stringResource

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
        -1 -> stringResource(Res.string.text_hora)
        0 -> stringResource(Res.string.text_tenpai)
        else -> stringResource(
            Res.string.text_shanten_num, shantenNum
        )
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
    get() = when (this.name) {
        Yakus.Tsumo.name -> Res.string.label_yaku_tsumo
        Yakus.Pinhu.name -> Res.string.label_yaku_pinhu
        Yakus.Tanyao.name -> Res.string.label_yaku_tanyao
        Yakus.Ipe.name -> Res.string.label_yaku_ipe
        Yakus.SelfWind.name -> Res.string.label_yaku_self_wind
        Yakus.RoundWind.name -> Res.string.label_yaku_round_wind
        Yakus.Chankan.name -> Res.string.label_yaku_chankan
        Yakus.Haku.name -> Res.string.label_yaku_haku
        Yakus.Hatsu.name -> Res.string.label_yaku_hatsu
        Yakus.Chun.name -> Res.string.label_yaku_chun
        Yakus.Sanshoku.name -> Res.string.label_yaku_sanshoku
        Yakus.Ittsu.name -> Res.string.label_yaku_ittsu
        Yakus.Chanta.name -> Res.string.label_yaku_chanta
        Yakus.Chitoi.name -> Res.string.label_yaku_chitoi
        Yakus.Toitoi.name -> Res.string.label_yaku_toitoi
        Yakus.Sananko.name -> Res.string.label_yaku_sananko
        Yakus.Honroto.name -> Res.string.label_yaku_honroto
        Yakus.Sandoko.name -> Res.string.label_yaku_sandoko
        Yakus.Sankantsu.name -> Res.string.label_yaku_sankantsu
        Yakus.Shosangen.name -> Res.string.label_yaku_shosangen
        Yakus.Honitsu.name -> Res.string.label_yaku_honitsu
        Yakus.Junchan.name -> Res.string.label_yaku_junchan
        Yakus.Ryanpe.name -> Res.string.label_yaku_ryanpe
        Yakus.Chinitsu.name -> Res.string.label_yaku_chinitsu
        Yakus.Kokushi.name -> Res.string.label_yaku_kokushi
        Yakus.Suanko.name -> Res.string.label_yaku_suanko
        Yakus.Daisangen.name -> Res.string.label_yaku_daisangen
        Yakus.Tsuiso.name -> Res.string.label_yaku_tsuiso
        Yakus.Shousushi.name -> Res.string.label_yaku_shousushi
        Yakus.Lyuiso.name -> Res.string.label_yaku_lyuiso
        Yakus.Chinroto.name -> Res.string.label_yaku_chinroto
        Yakus.Sukantsu.name -> Res.string.label_yaku_sukantsu
        Yakus.Churen.name -> Res.string.label_yaku_churen
        Yakus.Daisushi.name -> Res.string.label_yaku_daisushi
        Yakus.ChurenNineWaiting.name -> Res.string.label_yaku_churenNineWaiting
        Yakus.SuankoTanki.name -> Res.string.label_yaku_suankoTanki
        Yakus.KokushiThirteenWaiting.name -> Res.string.label_yaku_kokushiThirteenWaiting
        Yakus.Tenhou.name -> Res.string.label_yaku_tenhou
        Yakus.Chihou.name -> Res.string.label_yaku_chihou
        Yakus.WRichi.name -> Res.string.label_yaku_wrichi
        Yakus.Richi.name -> Res.string.label_yaku_richi
        Yakus.Ippatsu.name -> Res.string.label_yaku_ippatsu
        Yakus.Rinshan.name -> Res.string.label_yaku_rinshan
        Yakus.Chankan.name -> Res.string.label_yaku_chankan
        Yakus.Haitei.name -> Res.string.label_yaku_haitei
        Yakus.Houtei.name -> Res.string.label_yaku_houtei
        else -> error("unknown yaku: $this")
    }

fun List<Tile>.remove(vararg tile: Tile): List<Tile> {
    return toMutableList().apply {
        tile.forEach { t ->
            val lastIndexOfTile = lastIndexOf(t)
            if (lastIndexOfTile != -1) {
                removeAt(lastIndexOfTile)
            }
        }
    }
}