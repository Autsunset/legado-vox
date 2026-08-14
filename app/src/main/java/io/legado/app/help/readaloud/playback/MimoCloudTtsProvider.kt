package io.legado.app.help.readaloud.playback

import com.google.gson.JsonParser
import io.legado.app.domain.model.readaloud.CloudTtsAudio
import io.legado.app.domain.model.readaloud.CloudTtsEngine
import io.legado.app.domain.model.readaloud.CloudTtsProviderType
import io.legado.app.domain.model.readaloud.CloudTtsSynthesisRequest
import io.legado.app.domain.model.readaloud.CloudTtsVoiceDescriptor
import io.legado.app.domain.model.readaloud.MimoTtsCatalog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

internal class MimoCloudTtsProvider(
    private val httpClient: OkHttpClient = cloudTtsHttpClient,
) : CloudTtsProvider {
    override val type = CloudTtsProviderType.Mimo

    override fun fetchVoices(engine: CloudTtsEngine): List<CloudTtsVoiceDescriptor> =
        when (engine.model.ifBlank { MimoTtsCatalog.MODEL_PRESET }) {
            MimoTtsCatalog.MODEL_PRESET -> BUILT_IN_VOICES
            else -> emptyList()
        }

    override fun synthesize(
        engine: CloudTtsEngine,
        request: CloudTtsSynthesisRequest,
    ): CloudTtsAudio {
        require(engine.apiKey.isNotBlank()) { "MiMo API Key 不能为空" }
        val speechText = MimoSpeechTextNormalizer.normalize(request.text)
        require(MimoSpeechTextNormalizer.hasSpeakableContent(speechText)) { "朗读文本不能为空" }
        val model = engine.model.ifBlank { MimoTtsCatalog.MODEL_PRESET }
        require(model in MimoTtsCatalog.MODELS) { "不支持的 MiMo 模型：$model" }
        require(request.voiceId.isNotBlank()) {
            when (model) {
                MimoTtsCatalog.MODEL_DESIGN -> "MiMo 音色设计描述不能为空"
                MimoTtsCatalog.MODEL_CLONE -> "MiMo 克隆音色参数不能为空"
                else -> "MiMo 音色不能为空"
            }
        }
        val options = MimoTtsOptionsCodec.decode(engine.optionsJson)
        val body = MimoTtsRequestFactory.build(
            model = model,
            voice = request.voiceId,
            speechText = speechText,
            style = request.style,
            instructions = request.instructions,
            options = options,
        )
        val httpRequest = Request.Builder()
            .url(MimoTtsRequestFactory.endpoint(engine.baseUrl))
            .header("api-key", engine.apiKey)
            .header("User-Agent", options.userAgent)
            .header("Content-Type", "application/json")
            .post(body.toRequestBody(JSON_MEDIA_TYPE))
            .build()
        return httpClient.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                val detail = response.body.string().trim().take(2_000)
                val message = buildString {
                    append("MiMo TTS 请求失败：HTTP ${response.code}")
                    if (detail.isNotBlank()) append("：$detail")
                }
                if (response.code == 429 || response.code in 500..599) {
                    throw IOException(message)
                }
                error(message)
            }
            val json = response.body.charStream().use(JsonParser::parseReader).asJsonObject
            val choices = json.getAsJsonArray("choices")
            val audioBase64 = choices?.takeIf { it.size() > 0 }?.get(0)?.asJsonObject
                ?.getAsJsonObject("message")?.getAsJsonObject("audio")?.get("data")
                ?.takeUnless { it.isJsonNull }?.asString?.takeIf(String::isNotBlank)
                ?: error("MiMo TTS 没有返回音频数据")
            val bytes = java.util.Base64.getDecoder().decode(audioBase64)
            require(bytes.isNotEmpty()) { "MiMo TTS 没有返回音频数据" }
            CloudTtsAudio(bytes, "wav", wavSampleRate(bytes) ?: SAMPLE_RATE)
        }
    }

    private fun wavSampleRate(bytes: ByteArray): Int? {
        if (bytes.size < 28 || bytes.copyOfRange(0, 4).toString(Charsets.US_ASCII) != "RIFF") {
            return null
        }
        return (bytes[24].toInt() and 0xff) or
            ((bytes[25].toInt() and 0xff) shl 8) or
            ((bytes[26].toInt() and 0xff) shl 16) or
            ((bytes[27].toInt() and 0xff) shl 24)
    }

    private companion object {
        const val SAMPLE_RATE = 24_000
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
        val STYLES = listOf(
            "开心", "悲伤", "生气", "恐惧", "惊讶", "兴奋", "委屈", "平静", "冷漠",
            "怅然", "欣慰", "无奈", "愧疚", "释然", "动情",
            "温柔", "高冷", "活泼", "严肃", "慵懒", "俏皮", "深沉", "干练",
            "磁性", "醇厚", "清亮", "空灵", "甜美", "沙哑",
            "夹子音", "御姐音", "正太音", "大叔音", "台湾腔",
            "粤语", "四川话", "悄悄话", "唱歌",
        )
        val BUILT_IN_VOICES = listOf(
            CloudTtsVoiceDescriptor("冰糖", "冰糖 · 甜美可爱女声", "zh-CN", "Female", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("茉莉", "茉莉 · 温柔知性女声", "zh-CN", "Female", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("苏打", "苏打 · 活力阳光男声", "zh-CN", "Male", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("白桦", "白桦 · 沉稳磁性男声", "zh-CN", "Male", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("Mia", "Mia · 英文女声", "en", "Female", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("Chloe", "Chloe · 英文女声", "en", "Female", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("Milo", "Milo · 英文男声", "en", "Male", STYLES, sampleRate = SAMPLE_RATE),
            CloudTtsVoiceDescriptor("Dean", "Dean · 英文男声", "en", "Male", STYLES, sampleRate = SAMPLE_RATE),
        )
    }
}
