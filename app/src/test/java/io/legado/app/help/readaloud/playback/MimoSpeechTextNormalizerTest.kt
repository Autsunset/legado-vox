package io.legado.app.help.readaloud.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MimoSpeechTextNormalizerTest {
    @Test
    fun `normalizes title wrappers to natural quotation marks`() {
        assertEquals("他说：“你好。”", MimoSpeechTextNormalizer.normalize("他说：【你好。】"))
        assertEquals("Read \"chapter one\".", MimoSpeechTextNormalizer.normalize("Read [chapter one]."))
    }

    @Test
    fun `detects punctuation only content`() {
        assertFalse(MimoSpeechTextNormalizer.hasSpeakableContent("……！？"))
        assertTrue(MimoSpeechTextNormalizer.hasSpeakableContent("第 1 章"))
    }
}
