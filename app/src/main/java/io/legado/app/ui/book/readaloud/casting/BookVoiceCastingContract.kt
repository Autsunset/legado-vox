package io.legado.app.ui.book.readaloud.casting

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class BookVoiceCastingUiState(
    val bookUrl: String,
    val isLoading: Boolean = true,
    val items: ImmutableList<VoiceCastingItemUi> = persistentListOf(),
    val voices: ImmutableList<VoiceOptionUi> = persistentListOf(),
    val picker: VoicePickerUi? = null,
    val rulesEditor: String? = null,
    val rulesError: String = "",
)

@Stable
data class VoiceCastingItemUi(
    val subjectType: String,
    val subjectId: String,
    val kind: CastingSubjectKind,
    val name: String,
    val description: String = "",
    val avatarUri: String? = null,
    val hasBinding: Boolean = false,
    val voiceName: String = "",
    val voiceAvailable: Boolean = false,
)

@Stable
data class VoiceOptionUi(
    val id: String,
    val name: String,
    val engineType: String,
    val engineName: String,
    val selectable: Boolean,
)

@Stable
data class VoicePickerUi(
    val subjectType: String,
    val subjectId: String,
    val kind: CastingSubjectKind,
    val name: String,
    val selectedVoiceId: String?,
)

enum class CastingSubjectKind {
    Narrator,
    UnknownMale,
    UnknownFemale,
    Unknown,
    Character,
}

sealed interface BookVoiceCastingIntent {
    data object OpenRules : BookVoiceCastingIntent
    data object DismissRules : BookVoiceCastingIntent
    data class EditRules(val json: String) : BookVoiceCastingIntent
    data object SaveRules : BookVoiceCastingIntent
    data object ImportRules : BookVoiceCastingIntent
    data object ExportRules : BookVoiceCastingIntent
    data class ImportRulesFile(val uri: android.net.Uri) : BookVoiceCastingIntent
    data class ExportRulesFile(val uri: android.net.Uri) : BookVoiceCastingIntent
    data object Refresh : BookVoiceCastingIntent
    data class OpenVoicePicker(val subjectType: String, val subjectId: String) :
        BookVoiceCastingIntent
    data object DismissVoicePicker : BookVoiceCastingIntent
    data class AssignVoice(val voiceId: String) : BookVoiceCastingIntent
    data object ClearBinding : BookVoiceCastingIntent
}

sealed interface BookVoiceCastingEffect {
    data object OpenRulesImportPicker : BookVoiceCastingEffect
    data object OpenRulesExportPicker : BookVoiceCastingEffect
    data class ShowToast(val message: String) : BookVoiceCastingEffect
}
