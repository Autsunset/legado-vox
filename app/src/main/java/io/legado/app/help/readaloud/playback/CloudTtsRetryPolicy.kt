package io.legado.app.help.readaloud.playback

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

internal object CloudTtsRetryPolicy {
    suspend fun <T> withRetry(
        retryCount: Int,
        baseDelayMs: Long,
        beforeAttempt: (suspend () -> Unit)? = null,
        block: suspend () -> T,
    ): T {
        val maxAttempts = retryCount.coerceAtLeast(0) + 1
        var attempt = 0
        while (true) {
            try {
                if (attempt > 0) delay(baseDelayMs.coerceAtLeast(0L) * attempt * attempt)
                beforeAttempt?.invoke()
                return block()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (error !is IOException || attempt >= maxAttempts - 1) throw error
                attempt++
            }
        }
    }
}

internal class CloudTtsRequestThrottle(
    private val now: () -> Long = System::currentTimeMillis,
    private val wait: suspend (Long) -> Unit = { delay(it) },
) {
    private val mutexes = HashMap<String, Mutex>()
    private val lastPassedAt = ConcurrentHashMap<String, Long>()

    suspend fun waitTurn(key: String, intervalMs: Long) {
        if (intervalMs <= 0L) return
        val mutex = synchronized(mutexes) { mutexes.getOrPut(key) { Mutex() } }
        mutex.withLock {
            val elapsed = now() - (lastPassedAt[key] ?: 0L)
            if (elapsed in 0 until intervalMs) wait(intervalMs - elapsed)
            lastPassedAt[key] = now()
        }
    }
}
