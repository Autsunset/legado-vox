package io.legado.app.help.readaloud.playback

import org.junit.Assert.*
import org.junit.Test

class AudioPrefetchWindowTest {
    @Test fun smallDialogueCuesUseCharacterBudgetInsteadOfThreeRequests() {
        val texts = List(100) { "你好。" }
        assertEquals(0..19, AudioPrefetchWindow.indices(texts, 0, 60))
        assertEquals(5..24, AudioPrefetchWindow.indices(texts, 5, 60))
    }

    @Test fun windowIsBoundedAtBookEndAndForLargePageBudgets() {
        assertEquals(98..99, AudioPrefetchWindow.indices(List(100) { "甲" }, 98, 60))
        assertEquals(0..63, AudioPrefetchWindow.indices(List(100) { "甲" }, 0, 1000))
        assertTrue(AudioPrefetchWindow.indices(emptyList(), 0, 20).isEmpty())
    }
}
