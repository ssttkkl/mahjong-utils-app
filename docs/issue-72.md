# Winning hand analysis: issue #72

## Rules and ownership

The [report](https://github.com/ssttkkl/mahjong-utils-app/issues/72) contains three assertions and no example hand. The app README specifies Mahjong Soul/Tenhou defaults, with some configurable scoring options; it does not select WRC rules.

- Riichi + Ippatsu + Chankan is possible: the opponent's pon can precede riichi, and robbing the subsequent added kan interrupts its completion. [EMA Riichi 2025, §4.2.1, p23](https://mahjong-europe.org/portal/images/docs/Riichi-rules-2025-EN.pdf) explicitly permits the combination ([readable copy](https://loicallegre.github.io/assets/mahjong/Riichi-rules-2025-EN.pdf)).
- The kan declarer holds all four copies immediately before the robbery. The winner cannot already hold another copy, including in a meld. A completed-hand input already includes the winning tile; that one copy must be excluded. Red and ordinary fives count as the same tile kind.
- Concealed kan is ankan; melded kan is minkan. The English label values were swapped; the boolean-to-model mapping was correct.

Do not generalize the reporter's first assertion into a new double-riichi policy. The app has no kan-event history or rule option for robbing concealed kan. [Mahjong Soul's official ranked rules](https://mahjongsoul.com/news/46) permit kokushi robbing concealed kan; [WRC 2025 §8.11.1](https://www.worldriichi.org/wrc-rules) forbids it. This change preserves the existing variant surface. A missing-tile kokushi passes the physical-count check, while thirteen-sided kokushi cannot rob a kan because it already holds that tile.

All three changes belong to **app**. Core `HoraArgs` explicitly documents `extraYaku` as caller-supplied without yaku-legality validation, and core can already score the legal combination. No core release or dependency update is required; the app retains **0.7.7**. The unpublished 0.8.0 is unrelated.

## Reproduction and complete path

For a valid ordinary example, enter `234m456p789s11z23m`, win by ron on `1m`, and select Riichi, Ippatsu and Chankan. Before the fix, selecting either Ippatsu or Chankan disables the other.

`HoraFormComponents.ExtraYaku` uses `HoraFormState.allExtraYaku`; its `LaunchedEffect` removes unavailable selections. The reciprocal conflict-matrix entries caused the first bug. The Chankan availability branch previously checked only tsumo, so another copy in hand or a meld was accepted. `onCheck` also accepted directly restored invalid Chankan values.

`onCheck` normalizes a 3k+1 input by adding the winning tile, calls core `HoraArgs.validate`, then returns app `models.hora.HoraArgs`. Its `calc()` passes the arguments to core `hora()`, which computes shanten, enumerates winning patterns, merges caller-supplied extra yaku and scores the result. General tile-count validation cannot account for an opponent's unrepresented kan, so app must perform this extra-yaku check.

For the second bug, use `123m456p789s11z23m`, ron `1m`, and Chankan: an existing `1m` makes the robbery impossible. Ordinary ron on that hand remains legal. Both 13- and 14-tile inputs are covered.

`HoraFormComponents.ankanOptions` maps `label_ankan` to `true` and `label_minkan` to `false`; `FuroModel.toFuro()` forwards this to core `Furo(tiles, ankan)`. Only the English resource values required correction.

## Regression checks

```sh
# Configure env.properties to enable only desktop before running:
./gradlew :shared:desktopTest --tests '*HoraFormStateTest'
python3 -m unittest discover -s tests -v
```

Eight common Kotlin tests cover both selection orders and actual core scoring; valid/invalid 13- and 14-tile forms; auto-detected winning tile; copies in open/concealed melds; restored values; red fives; kokushi; and existing ordinary-ron/tsumo restrictions. Two Python tests check English labels against the corresponding action resources and validation-message presence in all three supported languages.

Local verification:

- **Full project desktop regression: PASS**, eight tests, zero failures/errors. `:shared:desktopTest --tests '*HoraFormStateTest'` compiled the real project and ran with its Kotlin 2.2.0 and unchanged core 0.7.7. A temporary Gradle init script added public dependency mirrors after the default Maven Central endpoint returned HTTP 403; no repository dependency configuration changed. The test JVM's `user.home` was redirected to `/tmp/issue72-test-home` to protect existing app data.
- The same eight tests also passed in an isolated compiler harness using the actual form source, real Compose runtime 1.8.0, coroutines 1.10.1 and published core JVM 0.7.7. Generated resource identifiers, app argument storage and datastore boundaries were substituted only in that additional harness. It used available Kotlin 2.4.20; the full project run above verifies the project's own compiler and integration. The original form failed six of those eight harness tests.
- Both Python resource checks and `git diff --check` passed.

Android device/emulator, iOS, desktop UI and Wasm rendering have not been exercised. Initial PR checks also show a Gemini reviewer infrastructure failure (CLI rejects an untrusted working directory) and a Vercel preview failure (cause not diagnosed); these are not reported as passing. Platform packaging jobs were still running at the last check.
