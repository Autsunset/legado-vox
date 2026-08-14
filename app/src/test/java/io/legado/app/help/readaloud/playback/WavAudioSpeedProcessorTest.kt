package io.legado.app.help.readaloud.playback

import io.legado.app.domain.model.readaloud.CloudTtsAudio
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sin
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WavAudioSpeedProcessorTest {
    @Test
    fun `one times speed keeps original bytes`() {
        val bytes = wav(sampleCount = 2_400)

        val result = WavAudioSpeedProcessor.process(CloudTtsAudio(bytes, "wav", 24_000), 1f)

        assertArrayEquals(bytes, result.bytes)
    }

    @Test
    fun `faster speed shortens pcm and keeps valid wav sizes`() {
        val bytes = wav(sampleCount = 24_000)

        val result = WavAudioSpeedProcessor.process(CloudTtsAudio(bytes, "wav", 24_000), 2.5f)
        val dataSize = littleEndianInt(result.bytes, 40)

        assertTrue(dataSize < 24_000 * 2)
        assertTrue(dataSize > 8_000)
        assertEquals(result.bytes.size - 8, littleEndianInt(result.bytes, 4))
        assertEquals(result.bytes.size - 44, dataSize)
        assertEquals(24_000, result.sampleRate)
    }

    @Test
    fun `non pcm wav is left untouched`() {
        val bytes = wav(sampleCount = 2_400).also { it[20] = 3 }

        val result = WavAudioSpeedProcessor.process(CloudTtsAudio(bytes, "wav", 24_000), 1.5f)

        assertArrayEquals(bytes, result.bytes)
    }

    private fun wav(sampleCount: Int, sampleRate: Int = 24_000): ByteArray {
        val pcm = ByteBuffer.allocate(sampleCount * 2).order(ByteOrder.LITTLE_ENDIAN).apply {
            repeat(sampleCount) { index ->
                putShort((sin(index * 2.0 * Math.PI * 220 / sampleRate) * Short.MAX_VALUE * 0.25).toInt().toShort())
            }
        }.array()
        return ByteBuffer.allocate(44 + pcm.size).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray())
            putInt(36 + pcm.size)
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
            putInt(pcm.size)
            put(pcm)
        }.array()
    }

    private fun littleEndianInt(bytes: ByteArray, offset: Int): Int =
        ByteBuffer.wrap(bytes, offset, 4).order(ByteOrder.LITTLE_ENDIAN).int
}
