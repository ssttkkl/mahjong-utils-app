package io.ssttkkl.mahjongutils.app.models.base

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class History<T>(
    val args: T,
    val createTime: Instant = Clock.System.now()
)