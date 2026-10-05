package io.legado.app.domain.model.readaloud

data class ReadAloudPlaybackCue(
    val text: String,
    val chapterStart: Int,
    val chapterEnd: Int,
    val paragraphIndex: Int,
    val voice: ReadAloudVoice?,
    val fallbackVoices: List<ReadAloudVoice>,
    val roleType: SpeechRoleType,
    val characterId: String?,
    val emotion: String = "",
    val characterPerformance: CharacterPerformanceProfile? = null,
    val context: String = "",
) {
    init {
        require(chapterStart >= 0)
        require(chapterEnd == chapterStart + text.length)
    }
}

data class ReadAloudPlaybackCursor(
    val cueIndex: Int,
    val offset: Int,
)

data class ReadAloudPlaybackInfo(
    val chapterPosition: Int = 0,
    val chapterLength: Int = 0,
    val text: String = "",
    val engineName: String = "",
    val characterName: String = "",
    val roleType: SpeechRoleType = SpeechRoleType.Narrator,
)

/** Position-based playback queue independent from reader pagination. */
class ReadAloudPlaybackQueue private constructor(
    val cues: List<ReadAloudPlaybackCue>,
) {

    val isEmpty: Boolean get() = cues.isEmpty()

    fun cursorAt(chapterPosition: Int): ReadAloudPlaybackCursor? {
        if (cues.isEmpty()) return null
        val position = chapterPosition.coerceAtLeast(0)
        val containingIndex = cues.binarySearch { cue ->
            when {
                cue.chapterEnd <= position -> -1
                cue.chapterStart > position -> 1
                else -> 0
            }
        }
        val index = if (containingIndex >= 0) {
            containingIndex
        } else {
            (-containingIndex - 1).coerceAtMost(cues.lastIndex)
        }
        val cue = cues[index]
        return ReadAloudPlaybackCursor(
            cueIndex = index,
            offset = (position - cue.chapterStart).coerceIn(0, cue.text.length),
        )
    }

    fun previous(cursor: ReadAloudPlaybackCursor): ReadAloudPlaybackCursor? =
        (cursor.cueIndex - 1).takeIf { it in cues.indices }
            ?.let { ReadAloudPlaybackCursor(it, 0) }

    fun next(cursor: ReadAloudPlaybackCursor): ReadAloudPlaybackCursor? =
        (cursor.cueIndex + 1).takeIf { it in cues.indices }
            ?.let { ReadAloudPlaybackCursor(it, 0) }

    companion object {
        val Empty = ReadAloudPlaybackQueue(emptyList())

        fun from(plan: List<SpeechPlanItem>, maxChunkChars: Int = Int.MAX_VALUE): ReadAloudPlaybackQueue {
            if (plan.isEmpty()) return Empty
            val paragraphs = plan.sortedBy { it.segment.chapterPosition }.groupBy { it.segment.paragraphIndex }
                .mapValues { (_, items) -> items.joinToString("") { it.segment.text } }
            val paragraphIndices = paragraphs.keys.sorted()
            val cues = plan.map { item ->
                val segment = item.segment
                ReadAloudPlaybackCue(
                    text = segment.text,
                    chapterStart = segment.chapterPosition,
                    chapterEnd = segment.chapterPosition + segment.text.length,
                    paragraphIndex = segment.paragraphIndex,
                    voice = item.voice,
                    fallbackVoices = item.fallbackVoices,
                    roleType = segment.roleType,
                    characterId = segment.characterId,
                    emotion = segment.emotion,
                    characterPerformance = item.characterPerformance,
                    context = paragraphIndices.indexOf(segment.paragraphIndex).let { index ->
                        listOf(
                            paragraphIndices.getOrNull(index - 1)?.let { paragraphs[it].orEmpty().takeLast(350) }.orEmpty(),
                            paragraphs[segment.paragraphIndex].orEmpty().take(500),
                            paragraphIndices.getOrNull(index + 1)?.let { paragraphs[it].orEmpty().take(350) }.orEmpty(),
                        ).filter(String::isNotBlank).joinToString("\n")
                    },
                )
            }.sortedWith(compareBy(ReadAloudPlaybackCue::chapterStart, ReadAloudPlaybackCue::chapterEnd))
            require(cues.zipWithNext().none { (left, right) -> left.chapterEnd > right.chapterStart }) {
                "Playback cues must not overlap"
            }
            val limit = maxChunkChars.coerceAtLeast(40)
            val boundedCues = cues.flatMap { cue ->
                val parts = mutableListOf<ReadAloudPlaybackCue>()
                var start = 0
                while (start < cue.text.length) {
                    var end = minOf(cue.text.length.toLong(), start.toLong() + limit).toInt()
                    if (end < cue.text.length) {
                        val sentence = (end - 1 downTo minOf(end - 1, start + limit / 2)).firstOrNull { cue.text[it] in "。！？；.!?;" }
                        if (sentence != null) end = sentence + 1
                        if (end > start && cue.text[end - 1].isHighSurrogate() && cue.text[end].isLowSurrogate()) end--
                    }
                    parts += cue.copy(text = cue.text.substring(start, end), chapterStart = cue.chapterStart + start, chapterEnd = cue.chapterStart + end)
                    start = end
                }
                parts
            }
            return ReadAloudPlaybackQueue(boundedCues)
        }
    }
}
