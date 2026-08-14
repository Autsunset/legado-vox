package io.legado.app.help.readaloud.playback

import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CloudTtsRetryPolicyTest {
    @Test
    fun `retries transient network errors`() = runBlocking {
        var attempts = 0

        val value = CloudTtsRetryPolicy.withRetry(retryCount = 2, baseDelayMs = 0) {
            attempts++
            if (attempts < 3) throw IOException("temporary")
            "ok"
        }

        assertEquals("ok", value)
        assertEquals(3, attempts)
    }

    @Test
    fun `throttle isolates independent engines`() = runBlocking {
        var time = 1_000L
        val waits = mutableListOf<Long>()
        val throttle = CloudTtsRequestThrottle(
            now = { time },
            wait = { duration -> waits += duration; time += duration },
        )
        throttle.waitTurn("engine-a", 1_000)
        throttle.waitTurn("engine-b", 1_000)

        assertEquals(emptyList<Long>(), waits)
    }

    @Test
    fun `throttle spaces requests for the same engine`() = runBlocking {
        var time = 1_000L
        val waits = mutableListOf<Long>()
        val throttle = CloudTtsRequestThrottle(
            now = { time },
            wait = { duration -> waits += duration; time += duration },
        )
        throttle.waitTurn("engine-a", 1_000)
        throttle.waitTurn("engine-a", 1_000)
        throttle.waitTurn("engine-a", 1_000)

        assertEquals(listOf(1_000L, 1_000L), waits)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `does not retry permanent request errors`(): Unit = runBlocking {
        var attempts = 0
        try {
            CloudTtsRetryPolicy.withRetry(retryCount = 5, baseDelayMs = 0) {
                attempts++
                throw IllegalArgumentException("bad request")
            }
        } finally {
            assertEquals(1, attempts)
        }
    }
}
