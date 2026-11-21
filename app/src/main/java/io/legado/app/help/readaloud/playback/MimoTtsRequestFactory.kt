package io.legado.app.help.readaloud.playback

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.legado.app.domain.model.readaloud.CloudTtsProviderType
import io.legado.app.domain.model.readaloud.MimoTtsCatalog
import io.legado.app.domain.model.readaloud.MimoTtsOptions
import io.legado.app.help.readaloud.segment.SpeechEmotionDetector

internal object MimoTtsRequestFactory {
    fun dialectTag(style: String?, model: String = MimoTtsCatalog.MODEL_PRESET): String? {
        if (model == MimoTtsCatalog.MODEL_DESIGN) return null
        return when (val dialect = style?.trim()) {
            "粤语", "粵語" -> "粤语"
            "四川话", "东北话", "河南话" -> dialect
            else -> null
        }
    }

    fun build(
        model: String,
        voice: String,
        speechText: String,
        style: String,
        instructions: String,
        options: MimoTtsOptions,
        context: String = "",
    ): String {
        val dialect = dialectTag(style, model)
        // Raw neighboring prose can leak into generated audio even with "do not read" prompts.
        val contextStyle = if (style.isBlank() && context.isNotBlank()) {
            CloudTtsEmotionMapper.map(
                CloudTtsProviderType.Mimo,
                SpeechEmotionDetector.detect(speechText, context).storageValue,
            ).style
        } else ""
        val directions = listOf(instructions, style.takeIf { dialect == null }.orEmpty(), contextStyle)
            .map(String::trim)
            .filter { it.isNotBlank() && it != NO_STYLE }
            .joinToString("。")
        val messages = JsonArray()
        val audio = JsonObject().apply { addProperty("format", "wav") }
        if (model == MimoTtsCatalog.MODEL_DESIGN) {
            messages.add(message("user", listOf(voice.trim(), directions)
                .filter(String::isNotBlank)
                .joinToString("。")))
            messages.add(message("assistant", speechText))
        } else {
            messages.add(message("user", directions))
            messages.add(message("assistant", dialect?.let { "($it)$speechText" } ?: speechText))
            audio.addProperty("voice", voice)
        }
        return JsonObject().apply {
            addProperty("model", model)
            addProperty("stream", false)
            add("messages", messages)
            add("audio", audio)
            addProperty("temperature", options.temperature)
        }.toString()
    }

    fun endpoint(value: String): String {
        val baseUrl = value.trim().ifBlank { DEFAULT_BASE_URL }.trimEnd('/')
        return if (baseUrl.endsWith(API_PATH)) {
            baseUrl
        } else {
            "${baseUrl.removeSuffix("/v1")}$API_PATH"
        }
    }

    private fun message(role: String, content: String) = JsonObject().apply {
        addProperty("role", role)
        addProperty("content", content)
    }

    private const val DEFAULT_BASE_URL = "https://api.xiaomimimo.com"
    private const val API_PATH = "/v1/chat/completions"
    private const val NO_STYLE = "无"
}
