from pathlib import Path
from fontTools import subset

subset_dest = "composeApp/src/desktopAndWasmJsMain/composeResources/font"

basic_extra_text = "".join(map(chr,range(0x00,0x7f)))  # 基本拉丁字母

res = {
    "en": {
        "strings": "composeApp/src/commonMain/composeResources/values/strings.xml",
        "origin_fonts": [
            "originFonts/NotoSansSC-Regular.ttf",
            "originFonts/NotoSansSC-Bold.ttf"
        ],
        "extra_text": basic_extra_text
    },
    "zh": {
        "strings": "composeApp/src/commonMain/composeResources/values-zh/strings.xml",
        "origin_fonts": [
            "originFonts/NotoSansSC-Regular.ttf",
            "originFonts/NotoSansSC-Bold.ttf"
        ],
        "extra_text": basic_extra_text+"年月日"
    },
    "ja": {
        "strings": "composeApp/src/commonMain/composeResources/values-ja/strings.xml",
        "origin_fonts": [
            "originFonts/NotoSansJP-Regular.ttf",
            "originFonts/NotoSansJP-Bold.ttf"
        ],
        "extra_text": basic_extra_text
    },
}

for lang in res:
    text = res[lang]["extra_text"]
    with open(res[lang]["strings"], encoding="utf-8") as f:
        text += f.read()

    for font in res[lang]["origin_fonts"]:
        font_name = Path(font).name
        args = [
            font,
            "--text=" + text,
            "--no-layout-closure",
            f"--output-file={subset_dest}/{font_name}",
#             "--flavor=woff2",
        ]

        subset.main(args)
