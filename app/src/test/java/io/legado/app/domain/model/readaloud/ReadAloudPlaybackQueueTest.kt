package io.legado.app.domain.model.readaloud

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReadAloudPlaybackQueueTest {
    @Test
    fun `shuffled plan remains in chapter order after chunking with context separate from speech`() {
        val before = "他打开房门。"
        val dialogue = "“不要走。”".repeat(40)
        val after = "她转身离开。"
        val queue = ReadAloudPlaybackQueue.from(
            listOf(
                item(after, before.length + dialogue.length, 2),
                item(dialogue, before.length, 1, SpeechRoleType.Character),
                item(before, 0, 0),
            ),
            180,
        )

        assertEquals(before + dialogue + after, queue.cues.joinToString("") { it.text })
        queue.cues.zipWithNext().forEach { (left, right) ->
            assertEquals(left.chapterEnd, right.chapterStart)
        }
        assertEquals(listOf(0, 1, 1, 2), queue.cues.map { it.paragraphIndex })
    }

    @Test fun `long cues split without losing offsets roles or paragraph context`() {
        val text = "她说。".repeat(100) + "😀结尾。"
        val chunks = ReadAloudPlaybackQueue.from(listOf(item("她含泪。", 0, 0), item(text, 5, 1, SpeechRoleType.Character), item("他转身。", 5 + text.length, 2)), 180).cues
        val dialogue = chunks.filter { it.roleType == SpeechRoleType.Character }
        assertEquals(text, dialogue.joinToString("") { it.text })
        org.junit.Assert.assertTrue(dialogue.all { it.text.length <= 180 && it.context.contains("她含泪。") && it.context.contains("他转身。") })
        assertEquals(5, dialogue.first().chapterStart)
        assertEquals(5 + text.length, dialogue.last().chapterEnd)
    }


    private val queue = ReadAloudPlaybackQueue.from(
        listOf(
            item("旁白", 10, 0),
            item("“你好”", 12, 0, SpeechRoleType.Character),
            item("回答。", 30, 1),
        )
    )

    @Test
    fun `finds cue and offset by absolute chapter position`() {
        assertEquals(ReadAloudPlaybackCursor(0, 1), queue.cursorAt(11))
        assertEquals(ReadAloudPlaybackCursor(1, 2), queue.cursorAt(14))
    }

    @Test
    fun `moves gaps to next cue and clamps after chapter content`() {
        assertEquals(ReadAloudPlaybackCursor(2, 0), queue.cursorAt(20))
        assertEquals(ReadAloudPlaybackCursor(2, 3), queue.cursorAt(100))
    }

    @Test
    fun `navigates without paragraph assumptions`() {
        val middle = ReadAloudPlaybackCursor(1, 2)
        assertEquals(ReadAloudPlaybackCursor(0, 0), queue.previous(middle))
        assertEquals(ReadAloudPlaybackCursor(2, 0), queue.next(middle))
        assertNull(queue.previous(ReadAloudPlaybackCursor(0, 0)))
        assertNull(queue.next(ReadAloudPlaybackCursor(2, 0)))
    }

    private fun item(
        text: String,
        start: Int,
        paragraph: Int,
        role: SpeechRoleType = SpeechRoleType.Narrator,
    ): SpeechPlanItem = SpeechPlanItem(
        segment = ChapterSpeechSegment(
            id = "$start",
            analysisId = "analysis",
            bookUrl = "book",
            chapterIndex = 1,
            paragraphIndex = paragraph,
            start = 0,
            end = text.length,
            chapterPosition = start,
            text = text,
            roleType = role,
            source = SpeechResolutionSource.Rule,
        ),
        voice = null,
        fallbackVoices = emptyList(),
    )
}
