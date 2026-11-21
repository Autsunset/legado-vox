package io.legado.app.help.readaloud.playback

import com.google.gson.JsonParser
import io.legado.app.domain.gateway.CloudTtsEngineGateway
import io.legado.app.domain.model.readaloud.CloudTtsEngine
import io.legado.app.domain.model.readaloud.CloudTtsProviderType
import io.legado.app.domain.model.readaloud.CloudTtsSynthesisRequest
import io.legado.app.domain.model.readaloud.ReadAloudVoice
import io.legado.app.domain.model.readaloud.SpeechEmotion
import java.net.InetSocketAddress
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import com.sun.net.httpserver.HttpServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class MimoCloudTtsProviderHttpTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `sends VoxEngine-compatible request and decodes returned wav`() = withMimoServer { engine, wav, captured ->
        val result = MimoCloudTtsProvider(OkHttpClient()).synthesize(
            engine = engine,
            request = CloudTtsSynthesisRequest(
                text = "他说：【你好。】",
                context = "前一句：她哭喊着。后一句：他转身离开。",
                voiceId = "茉莉",
                style = "开心",
                instructions = "温柔地朗读",
            ),
        )

        assertTrue(result.bytes.contentEquals(wav))
        assertEquals("wav", result.format)
        assertEquals(22_050, result.sampleRate)
        val request = captured.get()
        assertEquals("POST", request.method)
        assertEquals("secret-key", request.apiKey)
        assertEquals("legado-vox/test", request.userAgent)
        val json = JsonParser.parseString(request.body).asJsonObject
        assertEquals("mimo-v2.5-tts", json["model"].asString)
        assertFalse(json["stream"].asBoolean)
        assertEquals("茉莉", json.getAsJsonObject("audio")["voice"].asString)
        val messages = json.getAsJsonArray("messages")
        assertEquals("温柔地朗读。开心", messages[0].asJsonObject["content"].asString)
        assertEquals("他说：“你好。”", messages[1].asJsonObject["content"].asString)
        assertFalse(request.body.contains("上下文"))
        assertFalse(request.body.contains("她哭喊着"))
        assertFalse(request.body.contains("他转身离开"))
    }

    @Test
    fun `saved Cantonese reading style survives automatic emotion and bypasses old cache`() =
        withMimoServer { engine, wav, captured ->
            val gateway = object : CloudTtsEngineGateway {
                override fun observeAll() = flowOf(listOf(engine))
                override suspend fun getAll() = listOf(engine)
                override suspend fun get(id: String) = engine.takeIf { it.id == id }
                override suspend fun upsert(engine: CloudTtsEngine) = error("not used")
                override suspend fun delete(engine: CloudTtsEngine) = error("not used")
            }
            val synthesizer = CloudTtsAudioSynthesizer(
                gateway,
                CloudTtsProviderRegistry(listOf(MimoCloudTtsProvider(OkHttpClient()))),
            )
            val voice = ReadAloudVoice(
                id = "cantonese",
                engineType = ReadAloudVoice.ENGINE_CLOUD,
                engineId = engine.id,
                speakerId = "冰糖",
                displayName = "冰糖",
                traitsJson = """{"style":"粵語","automaticEmotion":true}""",
                emotionCatalogJson = """["粤语","开心"]""",
            )
            val text = "你今日想食啲咩呀？"
            val output = temporaryFolder.newFile()
            runBlocking {
                assertTrue(synthesizer.synthesize(
                    voice, text, output, styleOverride = SpeechEmotion.Cheerful.storageValue,
                ))
            }

            val messages = JsonParser.parseString(captured.get().body)
                .asJsonObject.getAsJsonArray("messages")
            assertEquals("开心", messages[0].asJsonObject["content"].asString)
            assertEquals("(粤语)$text", messages[1].asJsonObject["content"].asString)
            assertTrue(output.readBytes().contentEquals(wav))
            val dialectSuffix = CloudTtsAudioSynthesizer.cacheKeySuffix(voice)
            assertTrue(dialectSuffix.isNotEmpty())
            assertEquals(dialectSuffix, CloudTtsAudioSynthesizer.cacheKeySuffix(
                voice.copy(traitsJson = """{"style":"粤语"}"""),
            ))
            assertEquals("", CloudTtsAudioSynthesizer.cacheKeySuffix(
                voice.copy(traitsJson = """{"style":"开心"}"""),
            ))
        }

    private fun withMimoServer(
        test: (CloudTtsEngine, ByteArray, AtomicReference<CapturedRequest>) -> Unit,
    ) {
        val captured = AtomicReference<CapturedRequest>()
        val wav = pcm16MonoWav(sampleRate = 22_050, sampleCount = 20)
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            createContext("/v1/chat/completions") { exchange ->
                captured.set(
                    CapturedRequest(
                        method = exchange.requestMethod,
                        apiKey = exchange.requestHeaders.getFirst("api-key").orEmpty(),
                        userAgent = exchange.requestHeaders.getFirst("User-Agent").orEmpty(),
                        body = exchange.requestBody.bufferedReader().use { it.readText() },
                    )
                )
                val response = """{"choices":[{"message":{"audio":{"data":"${Base64.getEncoder().encodeToString(wav)}"}}}]}"""
                    .toByteArray()
                exchange.responseHeaders.add("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, response.size.toLong())
                exchange.responseBody.use { it.write(response) }
            }
            start()
        }
        try {
            test(
                CloudTtsEngine(
                    id = "mimo",
                    name = "MiMo",
                    provider = CloudTtsProviderType.Mimo,
                    baseUrl = "http://127.0.0.1:${server.address.port}",
                    apiKey = "secret-key",
                    model = "mimo-v2.5-tts",
                    optionsJson = """{"temperature":0.2,"userAgent":"legado-vox/test"}""",
                ),
                wav,
                captured,
            )
        } finally {
            server.stop(0)
        }
    }

    private fun pcm16MonoWav(sampleRate: Int, sampleCount: Int): ByteArray {
        val pcmSize = sampleCount * 2
        return java.nio.ByteBuffer.allocate(44 + pcmSize)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            .apply {
                put("RIFF".toByteArray())
                putInt(36 + pcmSize)
                put("WAVE".toByteArray())
                put("fmt ".toByteArray())
                putInt(16)
                putShort(1)
                putShort(1)
                putInt(sampleRate)
                putInt(sampleRate * 2)
                putShort(2)
                putShort(16)
                put("data".toByteArray())
                putInt(pcmSize)
                repeat(sampleCount) { putShort(it.toShort()) }
            }.array()
    }

    private data class CapturedRequest(
        val method: String,
        val apiKey: String,
        val userAgent: String,
        val body: String,
    )
}
