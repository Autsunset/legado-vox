package io.legado.app.help.readaloud.playback

import android.annotation.SuppressLint
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.SonicAudioProcessor
import io.legado.app.domain.model.readaloud.CloudTtsAudio
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

/** Applies VoxEngine-style speed-without-pitch-shift to app-internal MiMo WAV audio. */
@SuppressLint("UnsafeOptInUsageError")
internal object WavAudioSpeedProcessor {
    fun process(audio: CloudTtsAudio, speed: Float): CloudTtsAudio {
        if (!speed.isFinite() || abs(speed - 1f) < 0.01f) return audio
        val wav = parsePcm16Wav(audio.bytes) ?: return audio
        val adjustedPcm = adjustPcm16(
            pcm = audio.bytes.copyOfRange(wav.dataOffset, wav.dataOffset + wav.dataSize),
            sampleRate = wav.sampleRate,
            channelCount = wav.channelCount,
            speed = speed.coerceIn(MIN_SPEED, MAX_SPEED),
        )
        if (adjustedPcm.isEmpty()) return audio

        val dataEnd = wav.dataOffset + wav.dataSize
        val suffixOffset = dataEnd + (wav.dataSize and 1)
        val output = ByteArrayOutputStream(
            audio.bytes.size - wav.dataSize + adjustedPcm.size + (adjustedPcm.size and 1)
        )
        output.write(audio.bytes, 0, wav.dataOffset)
        output.write(adjustedPcm)
        if (adjustedPcm.size and 1 != 0) output.write(0)
        if (suffixOffset < audio.bytes.size) {
            output.write(audio.bytes, suffixOffset, audio.bytes.size - suffixOffset)
        }
        val bytes = output.toByteArray()
        writeLittleEndianInt(bytes, wav.dataSizeOffset, adjustedPcm.size)
        writeLittleEndianInt(bytes, RIFF_SIZE_OFFSET, bytes.size - RIFF_CHUNK_OVERHEAD)
        return audio.copy(bytes = bytes, sampleRate = wav.sampleRate)
    }

    private fun adjustPcm16(
        pcm: ByteArray,
        sampleRate: Int,
        channelCount: Int,
        speed: Float,
    ): ByteArray {
        val processor = SonicAudioProcessor().apply {
            setSpeed(speed)
            setPitch(1f)
            configure(
                AudioProcessor.AudioFormat(
                    sampleRate,
                    channelCount,
                    C.ENCODING_PCM_16BIT,
                )
            )
            flush()
        }
        return try {
            val output = ByteArrayOutputStream((pcm.size / speed).toInt().coerceAtLeast(0))
            val input = ByteBuffer.allocateDirect(pcm.size)
                .order(ByteOrder.nativeOrder())
                .put(pcm)
                .apply(ByteBuffer::flip)
            while (input.hasRemaining()) {
                processor.queueInput(input)
                drain(processor, output)
            }
            processor.queueEndOfStream()
            drain(processor, output)
            output.toByteArray()
        } finally {
            processor.reset()
        }
    }

    private fun drain(processor: SonicAudioProcessor, output: ByteArrayOutputStream) {
        while (true) {
            val buffer = processor.output
            if (!buffer.hasRemaining()) return
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            output.write(bytes)
        }
    }

    private fun parsePcm16Wav(bytes: ByteArray): WavInfo? {
        if (bytes.size < MIN_WAV_SIZE || ascii(bytes, 0) != "RIFF" || ascii(bytes, 8) != "WAVE") {
            return null
        }
        var offset = RIFF_HEADER_SIZE
        var sampleRate = 0
        var channelCount = 0
        var bitsPerSample = 0
        var audioFormat = 0
        var dataOffset = -1
        var dataSize = 0
        var dataSizeOffset = -1
        while (offset + CHUNK_HEADER_SIZE <= bytes.size) {
            val chunkSize = littleEndianInt(bytes, offset + 4)
            if (chunkSize < 0) return null
            val chunkDataOffset = offset + CHUNK_HEADER_SIZE
            val chunkEnd = chunkDataOffset.toLong() + chunkSize
            if (chunkEnd > bytes.size) return null
            when (ascii(bytes, offset)) {
                "fmt " -> {
                    if (chunkSize < PCM_FMT_SIZE) return null
                    audioFormat = littleEndianShort(bytes, chunkDataOffset)
                    channelCount = littleEndianShort(bytes, chunkDataOffset + 2)
                    sampleRate = littleEndianInt(bytes, chunkDataOffset + 4)
                    bitsPerSample = littleEndianShort(bytes, chunkDataOffset + 14)
                }
                "data" -> {
                    dataOffset = chunkDataOffset
                    dataSize = chunkSize
                    dataSizeOffset = offset + 4
                    break
                }
            }
            offset = chunkDataOffset + chunkSize + (chunkSize and 1)
        }
        return WavInfo(
            sampleRate = sampleRate,
            channelCount = channelCount,
            dataOffset = dataOffset,
            dataSize = dataSize,
            dataSizeOffset = dataSizeOffset,
        ).takeIf {
            audioFormat == PCM_AUDIO_FORMAT &&
                bitsPerSample == PCM_16_BITS &&
                it.sampleRate > 0 &&
                it.channelCount in 1..2 &&
                it.dataOffset >= 0 &&
                it.dataSize > 0 &&
                it.dataSize % (it.channelCount * PCM_BYTES_PER_SAMPLE) == 0
        }
    }

    private fun ascii(bytes: ByteArray, offset: Int): String =
        if (offset < 0 || offset + 4 > bytes.size) ""
        else bytes.copyOfRange(offset, offset + 4).toString(Charsets.US_ASCII)

    private fun littleEndianShort(bytes: ByteArray, offset: Int): Int =
        (bytes[offset].toInt() and 0xff) or ((bytes[offset + 1].toInt() and 0xff) shl 8)

    private fun littleEndianInt(bytes: ByteArray, offset: Int): Int =
        (bytes[offset].toInt() and 0xff) or
            ((bytes[offset + 1].toInt() and 0xff) shl 8) or
            ((bytes[offset + 2].toInt() and 0xff) shl 16) or
            ((bytes[offset + 3].toInt() and 0xff) shl 24)

    private fun writeLittleEndianInt(bytes: ByteArray, offset: Int, value: Int) {
        bytes[offset] = value.toByte()
        bytes[offset + 1] = (value shr 8).toByte()
        bytes[offset + 2] = (value shr 16).toByte()
        bytes[offset + 3] = (value shr 24).toByte()
    }

    private data class WavInfo(
        val sampleRate: Int,
        val channelCount: Int,
        val dataOffset: Int,
        val dataSize: Int,
        val dataSizeOffset: Int,
    )

    private const val MIN_SPEED = 0.5f
    private const val MAX_SPEED = 4f
    private const val MIN_WAV_SIZE = 44
    private const val RIFF_HEADER_SIZE = 12
    private const val RIFF_CHUNK_OVERHEAD = 8
    private const val RIFF_SIZE_OFFSET = 4
    private const val CHUNK_HEADER_SIZE = 8
    private const val PCM_FMT_SIZE = 16
    private const val PCM_AUDIO_FORMAT = 1
    private const val PCM_16_BITS = 16
    private const val PCM_BYTES_PER_SAMPLE = 2
}
