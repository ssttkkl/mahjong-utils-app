package io.ssttkkl.mahjongutils.app.utils

import io.ssttkkl.mahjongutils.app.BuildKonfig

object SentryConfig {
    val dsn = BuildKonfig.SENTRY_DSN
    val release =
        "${BuildKonfig.APPLICATION_ID}@${BuildKonfig.VERSION_NAME}+${BuildKonfig.GIT_COMMIT_HASH}"
}