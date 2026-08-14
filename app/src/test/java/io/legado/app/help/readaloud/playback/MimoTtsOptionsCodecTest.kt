package io.legado.app.help.readaloud.playback

import io.legado.app.domain.model.readaloud.MimoTtsOptions
import org.junit.Assert.assertEquals
import org.junit.Test

class MimoTtsOptionsCodecTest {
    @Test
    fun `round trips audiobook synthesis options`() {
        val expected = MimoTtsOptions(
            temperature = 0.2f,
            userAgent = "legado-vox/test",
            retryCount = 4,
            retryBaseDelayMs = 3_000,
            requestIntervalMs = 5_000,
        )

        assertEquals(expected, MimoTtsOptionsCodec.decode(MimoTtsOptionsCodec.encode(expected)))
    }

    @Test
    fun `invalid options fall back and clamp to safe limits`() {
        val decoded = MimoTtsOptionsCodec.decode(
            """{
                "temperature":9,
                "userAgent":" ",
                "retryCount":99,
                "retryBaseDelayMs":-1,
                "requestIntervalMs":99999
            }""".trimIndent()
        )

        assertEquals(1.5f, decoded.temperature)
        assertEquals(MimoTtsOptions.DEFAULT_USER_AGENT, decoded.userAgent)
        assertEquals(5, decoded.retryCount)
        assertEquals(0L, decoded.retryBaseDelayMs)
        assertEquals(30_000L, decoded.requestIntervalMs)
    }
}
