package io.legado.app.help.readaloud.playback

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.legado.app.domain.model.readaloud.MimoTtsCatalog
import io.legado.app.domain.model.readaloud.MimoTtsOptions

internal object MimoTtsRequestFactory {
    fun build(
        model: String,
        voice: String,
        speechText: String,
        style: String,
        instructions: String,
        options: MimoTtsOptions,
        context: String = "",
    ): String {
        val contextInstruction = context.takeIf(String::isNotBlank)?.let {
            "以下小说上下文仅供理解人物情绪与语气，不要朗读或复述。只朗读 assistant 消息中的正文。\n<上下文>\n${it.take(1200)}\n</上下文>"
        }.orEmpty()
        val directions = listOf(instructions, style, contextInstruction)
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
            messages.add(message("assistant", speechText))
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
