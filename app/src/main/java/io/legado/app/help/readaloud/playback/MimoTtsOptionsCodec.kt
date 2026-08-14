package io.legado.app.help.readaloud.playback

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.legado.app.domain.model.readaloud.MimoTtsOptions

internal object MimoTtsOptionsCodec {
    fun decode(value: String): MimoTtsOptions {
        val json = runCatching {
            JsonParser.parseString(value).takeIf { it.isJsonObject }?.asJsonObject
        }.getOrNull() ?: JsonObject()
        return MimoTtsOptions(
            temperature = json.floatOrNull(KEY_TEMPERATURE)
                ?: MimoTtsOptions.DEFAULT_TEMPERATURE,
            userAgent = json.stringOrNull(KEY_USER_AGENT)?.trim()
                .orEmpty().ifBlank { MimoTtsOptions.DEFAULT_USER_AGENT },
            retryCount = json.intOrNull(KEY_RETRY_COUNT)
                ?: MimoTtsOptions.DEFAULT_RETRY_COUNT,
            retryBaseDelayMs = json.longOrNull(KEY_RETRY_BASE_DELAY_MS)
                ?: MimoTtsOptions.DEFAULT_RETRY_BASE_DELAY_MS,
            requestIntervalMs = json.longOrNull(KEY_REQUEST_INTERVAL_MS)
                ?: MimoTtsOptions.DEFAULT_REQUEST_INTERVAL_MS,
        ).normalized()
    }

    fun encode(options: MimoTtsOptions): String {
        val value = options.normalized()
        return JsonObject().apply {
            addProperty(KEY_TEMPERATURE, value.temperature)
            addProperty(KEY_USER_AGENT, value.userAgent)
            addProperty(KEY_RETRY_COUNT, value.retryCount)
            addProperty(KEY_RETRY_BASE_DELAY_MS, value.retryBaseDelayMs)
            addProperty(KEY_REQUEST_INTERVAL_MS, value.requestIntervalMs)
        }.toString()
    }

    private fun MimoTtsOptions.normalized() = copy(
        temperature = temperature.coerceIn(0f, 1.5f),
        userAgent = userAgent.trim().ifBlank { MimoTtsOptions.DEFAULT_USER_AGENT },
        retryCount = retryCount.coerceIn(0, 5),
        retryBaseDelayMs = retryBaseDelayMs.coerceIn(0L, 10_000L),
        requestIntervalMs = requestIntervalMs.coerceIn(0L, 30_000L),
    )

    private fun JsonObject.floatOrNull(name: String): Float? =
        runCatching { get(name)?.asFloat }.getOrNull()?.takeIf(Float::isFinite)

    private fun JsonObject.intOrNull(name: String): Int? =
        runCatching { get(name)?.asInt }.getOrNull()

    private fun JsonObject.longOrNull(name: String): Long? =
        runCatching { get(name)?.asLong }.getOrNull()

    private fun JsonObject.stringOrNull(name: String): String? =
        runCatching { get(name)?.asString }.getOrNull()

    private const val KEY_TEMPERATURE = "temperature"
    private const val KEY_USER_AGENT = "userAgent"
    private const val KEY_RETRY_COUNT = "retryCount"
    private const val KEY_RETRY_BASE_DELAY_MS = "retryBaseDelayMs"
    private const val KEY_REQUEST_INTERVAL_MS = "requestIntervalMs"
}
