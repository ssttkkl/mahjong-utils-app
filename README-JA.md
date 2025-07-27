リーチ麻雀計算機
======

[中文](README-ZH.md) [English](README.md)

![icon](fastlane/metadata/android/zh-CN/images/icon.png)

Web版：

- https://ssttkkl.github.io/mahjong-utils-app/
- https://mahjong-utils-app.vercel.app

Android版：

- [Releases](https://github.com/ssttkkl/mahjong-utils-app/releases)ページにアクセスし、最新リリースで
  **composeApp-release.apk**をダウンロードしてください。

デスクトップ版：

| OS            | インストーラ（Java不要）                            | JAR（Java 11+が必要）                                  |
|---------------|-------------------------------------------|---------------------------------------------------|
| Windows (x64) | mahjong-utils-app-xxx-x64.**exe**         | mahjong-utils-app-**windows-x64**-xxx-release.jar |
| macOS (arm64) | mahjong-utils-app-xxx-arm64.**app**       | mahjong-utils-app-**macos-arm64**-xxx-release.jar |
| macOS (x64)   | mahjong-utils-app-xxx-x64.**app**         | mahjong-utils-app-**macos-arm64**-xxx-release.jar |
| Linux (x64)   | mahjong-utils-app-xxx-**x86_64.AppImage** | mahjong-utils-app-**linux-x64**-xxx-release.jar   |

iOS版：

- [Releases](https://github.com/ssttkkl/mahjong-utils-app/releases)ページにアクセスし、最新リリースで
  **iosApp-unsigned.ipa**をダウンロードしてください。
- [Sideloadly](https://sideloadly.io/)やその他のサインツールを使って、IPAをサインしてインストールします。

## 機能

- 牌理：牌効率・良形効率（一向聴のみ）・向聴戻しを含みます。

![手牌効率](fastlane/metadata/android/ja-JP/images/tenInchScreenshots/1.png)

- 鳴き牌理：鳴き（チー・ポン・カン・パス）の牌効率・良形効率（一向聴のみ）・向聴戻しを含まれます。

![鳴き手効率](fastlane/metadata/android/ja-JP/images/tenInchScreenshots/2.png)

- 和了分析：初期設定は雀魂や天鳳のルールに従いますが、ある程度カスタマイズ可能です。

![和了分析](fastlane/metadata/android/ja-JP/images/tenInchScreenshots/4.png)  
![ルールのカスタマイズ](fastlane/metadata/android/ja-JP/images/tenInchScreenshots/7.png)

- 飜符による得点計算：初期設定は麻雀魂や天鳳のルールに従いますが、ある程度カスタマイズ可能です。

![飜符による得点計算](fastlane/metadata/android/ja-JP/images/tenInchScreenshots/5.png)

### 麻将牌の画像認識

麻将牌の入力が必要な入力欄では、画像認識機能が利用できます。入力キーボードからメニューを開くか、
入力欄を長押し/右クリックして「画像から認識」などのオプションを選択してください。
また、ショートカットキー `Ctrl`+`V`（macOSの場合は `Cmd`+`V`）でクリップボードから貼り付けることも可能です。

注意: 最適な認識結果を得るには、~雀魂（じゃんたま）~
のスクリーンショットをご利用ください（学習済みモデルは~雀魂~専用です）。

## 追加情報

アルゴリズムはローカルで実行され、ネットワークアクセスは行いません。

アプリはKotlin/MultiplatformおよびCompose Multiplatformを基に作られています。PRは歓迎します。

バグや翻訳ミスを発見した場合は、[issue](https://github.com/ssttkkl/mahjong-utils-app/issues)
に報告してください。

## ライセンス

MIT
