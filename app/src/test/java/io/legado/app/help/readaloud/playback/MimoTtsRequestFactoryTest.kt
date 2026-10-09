package io.legado.app.help.readaloud.playback

import com.google.gson.JsonParser
import io.legado.app.domain.model.readaloud.MimoTtsCatalog
import io.legado.app.domain.model.readaloud.MimoTtsOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MimoTtsRequestFactoryTest {
    @Test
    fun `context prose and labels never enter messages for any model`() {
        for (model in listOf(MimoTtsCatalog.MODEL_PRESET, MimoTtsCatalog.MODEL_CLONE, MimoTtsCatalog.MODEL_DESIGN)) {
            val json = JsonParser.parseString(MimoTtsRequestFactory.build(
                model, "voice", "不要走。", "悲伤", "", MimoTtsOptions(), "前一句：她哭喊着。后一句：他转身离开。"
            )).asJsonObject
            val messages = json.getAsJsonArray("messages")
            val directions = messages[0].asJsonObject["content"].asString
            assertEquals(if (model == MimoTtsCatalog.MODEL_DESIGN) "voice。悲伤" else "悲伤", directions)
            assertFalse(messages.toString().contains("上下文"))
            assertFalse(messages.toString().contains("她哭喊着"))
            assertFalse(messages.toString().contains("他转身离开"))
            assertEquals("不要走。", messages[1].asJsonObject["content"].asString)
        }
    }

    @Test
    fun `context is reduced to an emotion style when no style is selected`() {
        val json = JsonParser.parseString(MimoTtsRequestFactory.build(
            MimoTtsCatalog.MODEL_PRESET, "茉莉", "不要走。", "", "温柔地朗读",
            MimoTtsOptions(), "她哭喊着，眼泪不停地流下来。"
        )).asJsonObject
        val messages = json.getAsJsonArray("messages")

        assertEquals("温柔地朗读。悲伤", messages[0].asJsonObject["content"].asString)
        assertEquals("不要走。", messages[1].asJsonObject["content"].asString)
        assertFalse(messages.toString().contains("她哭喊着"))
    }

    @Test
    fun `neutral context does not produce extra directions`() {
        val json = JsonParser.parseString(MimoTtsRequestFactory.build(
            MimoTtsCatalog.MODEL_PRESET, "茉莉", "正文。", "", "", MimoTtsOptions(),
            "前文。当前段落。后文。"
        )).asJsonObject

        assertEquals("", json.getAsJsonArray("messages")[0].asJsonObject["content"].asString)
    }

    @Test
    fun `no style selection is preserved even when context contains emotion cues`() {
        val json = JsonParser.parseString(MimoTtsRequestFactory.build(
            MimoTtsCatalog.MODEL_PRESET, "茉莉", "正文。", "无", "", MimoTtsOptions(),
            "她哭喊着。"
        )).asJsonObject

        assertEquals("", json.getAsJsonArray("messages")[0].asJsonObject["content"].asString)
    }

    @Test
    fun `context word in the actual speech text is preserved`() {
        val text = "他解释了上下文的含义。"
        val json = JsonParser.parseString(MimoTtsRequestFactory.build(
            MimoTtsCatalog.MODEL_PRESET, "茉莉", text, "", "", MimoTtsOptions(),
            "前文。他解释了上下文的含义。后文。"
        )).asJsonObject

        assertEquals(text, json.getAsJsonArray("messages")[1].asJsonObject["content"].asString)
        assertTrue(json.getAsJsonArray("messages")[0].asJsonObject["content"].asString.isEmpty())
    }

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
