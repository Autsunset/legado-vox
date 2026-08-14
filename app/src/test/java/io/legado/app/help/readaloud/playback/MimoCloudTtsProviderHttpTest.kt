package io.legado.app.help.readaloud.playback

import com.google.gson.JsonParser
import io.legado.app.domain.model.readaloud.CloudTtsEngine
import io.legado.app.domain.model.readaloud.CloudTtsProviderType
import io.legado.app.domain.model.readaloud.CloudTtsSynthesisRequest
import java.net.InetSocketAddress
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import okhttp3.OkHttpClient
import com.sun.net.httpserver.HttpServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MimoCloudTtsProviderHttpTest {
    @Test
    fun `sends VoxEngine-compatible request and decodes returned wav`() {
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
            val result = MimoCloudTtsProvider(OkHttpClient()).synthesize(
                engine = CloudTtsEngine(
                    id = "mimo",
                    name = "MiMo",
                    provider = CloudTtsProviderType.Mimo,
                    baseUrl = "http://127.0.0.1:${server.address.port}",
                    apiKey = "secret-key",
                    model = "mimo-v2.5-tts",
                    optionsJson = """{"temperature":0.2,"userAgent":"legado-vox/test"}""",
                ),
                request = CloudTtsSynthesisRequest(
                    text = "他说：【你好。】",
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
