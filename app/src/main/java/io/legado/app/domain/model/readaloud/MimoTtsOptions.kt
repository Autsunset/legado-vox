package io.legado.app.domain.model.readaloud

/** MiMo AI TTS 的引擎级听书参数。 */
data class MimoTtsOptions(
    val temperature: Float = DEFAULT_TEMPERATURE,
    val userAgent: String = DEFAULT_USER_AGENT,
    val retryCount: Int = DEFAULT_RETRY_COUNT,
    val retryBaseDelayMs: Long = DEFAULT_RETRY_BASE_DELAY_MS,
    val requestIntervalMs: Long = DEFAULT_REQUEST_INTERVAL_MS,
) {
    companion object {
        const val DEFAULT_TEMPERATURE = 0.6f
        const val DEFAULT_USER_AGENT = "openclaw/unknown"
        const val DEFAULT_RETRY_COUNT = 3
        const val DEFAULT_RETRY_BASE_DELAY_MS = 2_000L
        const val DEFAULT_REQUEST_INTERVAL_MS = 0L
    }
}

object MimoTtsCatalog {
    const val MODEL_PRESET = "mimo-v2.5-tts"
    const val MODEL_CLONE = "mimo-v2.5-tts-voiceclone"
    const val MODEL_DESIGN = "mimo-v2.5-tts-voicedesign"

    val MODELS = listOf(MODEL_PRESET, MODEL_CLONE, MODEL_DESIGN)

    data class Endpoint(val id: String, val baseUrl: String)

    val ENDPOINTS = listOf(
        Endpoint("metered", "https://api.xiaomimimo.com"),
        Endpoint("token_cn", "https://token-plan-cn.xiaomimimo.com"),
        Endpoint("token_sgp", "https://token-plan-sgp.xiaomimimo.com"),
        Endpoint("token_eu", "https://token-plan-ams.xiaomimimo.com"),
    )
}
