package io.ssttkkl.mahjongutils.app.components.appscaffold

import io.ssttkkl.mahjongutils.app.utils.url.URLDecoder
import io.ssttkkl.mahjongutils.app.utils.url.URLEncoder

data class Url(
    val path: String,
    val params: Map<String, String>
) {
    override fun toString(): String {
        return buildString {
            append(path)
            if (params.isNotEmpty()) {
                append('?')
                params.entries.sortedBy { it.key }.forEachIndexed { idx, (k, v) ->
                    append(URLEncoder.encodeToUtf8(k))
                    append('=')
                    append(URLEncoder.encodeToUtf8(v))
                    if (idx != params.size - 1) {
                        append('&')
                    }
                }
            }
        }
    }

    companion object {
        fun parse(description: String): Url {
            if (!description.contains('?')) {
                return Url(description, emptyMap())
            }

            val (path, rest) = description.split('?', limit = 2)
            val params = rest.split('&')
                .filter { it.isNotEmpty() }
                .associate {
                    val (k, v) = it.split('=', limit = 2)
                    URLDecoder.decode(k) to URLDecoder.decode(v)
                }

            return Url(path, params)
        }
    }
}