package io.legado.app.help.readaloud.playback

/** A page budget keeps short dialogue fragments from exhausting a fixed cue-count buffer. */
object AudioPrefetchWindow {
    fun indices(texts: List<String>, start: Int, characterBudget: Int): IntRange {
        if (start !in texts.indices) return IntRange.EMPTY
        var characters = 0
        var end = start
        while (end < texts.size && end - start < 64) {
            characters += texts[end].length
            end++
            if (characters >= characterBudget.coerceAtLeast(1)) break
        }
        return start until end
    }
}
