package io.legado.app.help.readaloud.playback

import com.google.gson.JsonParser
import io.legado.app.domain.model.readaloud.MimoTtsCatalog
import io.legado.app.domain.model.readaloud.MimoTtsOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MimoTtsRequestFactoryTest {
    @Test
    fun `preset request matches VoxEngine chat completion contract`() {
        val json = JsonParser.parseString(
            MimoTtsRequestFactory.build(
                model = MimoTtsCatalog.MODEL_PRESET,
                voice = "茉莉",
                speechText = "她说：“你好。”",
                style = "开心",
                instructions = "温柔地朗读",
                options = MimoTtsOptions(temperature = 0.2f),
            )
        ).asJsonObject

        assertEquals(MimoTtsCatalog.MODEL_PRESET, json["model"].asString)
        assertFalse(json["stream"].asBoolean)
        assertEquals(0.2f, json["temperature"].asFloat)
        assertEquals("茉莉", json.getAsJsonObject("audio")["voice"].asString)
        assertEquals("wav", json.getAsJsonObject("audio")["format"].asString)
        val messages = json.getAsJsonArray("messages")
        assertEquals("user", messages[0].asJsonObject["role"].asString)
        assertEquals("温柔地朗读。开心", messages[0].asJsonObject["content"].asString)
        assertEquals("assistant", messages[1].asJsonObject["role"].asString)
        assertEquals("她说：“你好。”", messages[1].asJsonObject["content"].asString)
    }

    @Test
    fun `no style is not sent as a spoken direction`() {
        val json = JsonParser.parseString(
            MimoTtsRequestFactory.build(
                model = MimoTtsCatalog.MODEL_PRESET,
                voice = "冰糖",
                speechText = "正文",
                style = "无",
                instructions = "",
                options = MimoTtsOptions(),
            )
        ).asJsonObject

        assertEquals("", json.getAsJsonArray("messages")[0].asJsonObject["content"].asString)
    }

    @Test
    fun `endpoint accepts a host v1 prefix or full endpoint`() {
        assertEquals(
            "https://api.xiaomimimo.com/v1/chat/completions",
            MimoTtsRequestFactory.endpoint("https://api.xiaomimimo.com"),
        )
        assertEquals(
            "https://api.xiaomimimo.com/v1/chat/completions",
            MimoTtsRequestFactory.endpoint("https://api.xiaomimimo.com/v1"),
        )
        assertEquals(
            "https://proxy.example/v1/chat/completions",
            MimoTtsRequestFactory.endpoint("https://proxy.example/v1/chat/completions"),
        )
    }
}
