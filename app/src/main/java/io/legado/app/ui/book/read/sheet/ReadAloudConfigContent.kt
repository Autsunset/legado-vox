package io.legado.app.ui.book.read.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.legado.app.R
import io.legado.app.constant.ReadAloudBgMode
import io.legado.app.ui.book.read.ReadAloudConfigTab
import io.legado.app.ui.book.read.ReadBookIntent
import io.legado.app.ui.book.read.ReadBookUiState
import io.legado.app.ui.book.readaloud.player.ReadAloudPlayerIntent
import io.legado.app.ui.book.readaloud.player.ReadAloudPlayerUiState
import io.legado.app.ui.widget.components.settingItem.SliderSettingItem
import io.legado.app.ui.widget.components.settingItem.TinyClickableSettingItem
import io.legado.app.ui.widget.components.settingItem.TinyDropdownSettingItem
import io.legado.app.ui.widget.components.settingItem.TinySwitchSettingItem
import io.legado.app.ui.widget.components.tabRow.CardTabRow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ReadAloudConfigContent(
    state: ReadBookUiState,
    playerState: ReadAloudPlayerUiState,
    onIntent: (ReadBookIntent) -> Unit,
    onPlayerIntent: (ReadAloudPlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = state.readAloudConfigTab.ordinal,
        pageCount = { ReadAloudConfigTab.entries.size },
    )
    LaunchedEffect(state.readAloudConfigTab) {
        if (pagerState.currentPage != state.readAloudConfigTab.ordinal) {
            pagerState.animateScrollToPage(state.readAloudConfigTab.ordinal)
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                ReadAloudConfigTab.entries.getOrNull(page)?.let { tab ->
                    onIntent(ReadBookIntent.SelectReadAloudConfigTab(tab))
                }
            }
    }
    Column(modifier = modifier.fillMaxWidth()) {
        CardTabRow(
            modifier = Modifier.fillMaxWidth(),
            tabTitles = listOf(
                stringResource(R.string.read_aloud_settings_general_tab),
                stringResource(R.string.read_aloud_settings_voice_tab),
            ),
            selectedTabIndex = pagerState.currentPage,
            onTabSelected = { page ->
                ReadAloudConfigTab.entries.getOrNull(page)?.let { tab ->
                    onIntent(ReadBookIntent.SelectReadAloudConfigTab(tab))
                }
            }
        )
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp, bottom = 16.dp),
            ) {
                if (page == 0) {
                    TinyDropdownSettingItem(
                        title = stringResource(R.string.default_read_aloud_interface),
                        selectedValue = state.defaultReadAloudInterface,
                        displayEntries = arrayOf(
                            stringResource(R.string.read_aloud_interface_classic),
                            stringResource(R.string.read_aloud_interface_player),
                        ),
                        entryValues = arrayOf("classic", "player"),
                        description = stringResource(R.string.default_read_aloud_interface_summary),
                        onValueChange = { onIntent(ReadBookIntent.SetDefaultReadAloudInterface(it)) },
                    )
                    TinyDropdownSettingItem(
                        title = stringResource(R.string.read_aloud_player_background),
                        selectedValue = playerState.bgMode.toString(),
                        displayEntries = arrayOf(
                            stringResource(R.string.read_aloud_bg_solid),
                            stringResource(R.string.read_aloud_bg_blur),
                            stringResource(R.string.read_aloud_bg_flowing_light),
                            stringResource(R.string.read_aloud_bg_transparent),
                        ),
                        entryValues = arrayOf(
                            ReadAloudBgMode.Solid.toString(),
                            ReadAloudBgMode.Blur.toString(),
                            ReadAloudBgMode.FlowingLight.toString(),
                            ReadAloudBgMode.Transparent.toString(),
                        ),
                        onValueChange = { onPlayerIntent(ReadAloudPlayerIntent.SetBgMode(it.toInt())) },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.show_read_aloud_capsule),
                        description = stringResource(R.string.show_read_aloud_capsule_summary),
                        checked = state.showReadAloudCapsule,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetShowReadAloudCapsule(it))
                        },
                    )
                    if (state.showReadAloudCapsule) {
                        TinySwitchSettingItem(
                            title = stringResource(R.string.capsule_auto_collapse),
                            description = stringResource(R.string.capsule_auto_collapse_summary),
                            checked = state.capsuleAutoCollapse,
                            onCheckedChange = {
                                onIntent(ReadBookIntent.SetCapsuleAutoCollapse(it))
                            },
                        )
                    }
                    TinySwitchSettingItem(
                        title = stringResource(R.string.ignore_audio_focus_title),
                        description = stringResource(R.string.ignore_audio_focus_summary),
                        checked = state.readAloudIgnoreAudioFocus,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudIgnoreAudioFocus(it))
                        },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.pause_read_aloud_while_phone_calls_title),
                        description = stringResource(R.string.pause_read_aloud_while_phone_calls_summary),
                        checked = state.readAloudPauseOnPhoneCall,
                        enabled = state.readAloudIgnoreAudioFocus,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudPauseOnPhoneCall(it))
                        },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.read_aloud_wake_lock),
                        description = stringResource(R.string.read_aloud_wake_lock_summary),
                        checked = state.readAloudWakeLock,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudWakeLock(it))
                        },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.pref_media_button_per_next),
                        description = stringResource(R.string.pref_media_button_per_next_summary),
                        checked = state.readAloudMediaButtonPerNext,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudMediaButtonPerNext(it))
                        },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.read_aloud_by_page),
                        description = stringResource(R.string.read_aloud_by_page_summary),
                        checked = state.readAloudByPage,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudByPage(it))
                        },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.system_media_control_compatibility_change),
                        description = stringResource(R.string.system_media_control_compatibility_change_summary),
                        checked = state.readAloudSystemMediaCompat,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudSystemMediaCompat(it))
                        },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.stream_read_aloud_audio),
                        description = stringResource(R.string.stream_read_aloud_audio_summary),
                        checked = state.readAloudStreamAudio,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetReadAloudStreamAudio(it))
                        },
                    )
                    TinyClickableSettingItem(
                        title = stringResource(R.string.reset_read_aloud_capsule_position),
                        description = stringResource(R.string.reset_read_aloud_capsule_position_summary),
                        onClick = { onIntent(ReadBookIntent.ResetReadAloudCapsulePosition) },
                    )
                } else {
                    TinyClickableSettingItem(
                        title = stringResource(R.string.read_aloud_engines_and_voices),
                        description = stringResource(R.string.read_aloud_engines_and_voices_summary),
                        onClick = { onIntent(ReadBookIntent.OpenTtsEnginesAndVoices) },
                    )
                    TinyClickableSettingItem(
                        title = stringResource(R.string.tts_cache_manage),
                        description = stringResource(R.string.tts_cache_manage_summary),
                        onClick = { onIntent(ReadBookIntent.OpenTtsCache) },
                    )
                    TinyClickableSettingItem(
                        title = stringResource(R.string.read_aloud_character_casting),
                        description = stringResource(R.string.book_voice_casting_entry_summary),
                        onClick = { onIntent(ReadBookIntent.OpenBookVoiceCasting) },
                    )
                    TinyDropdownSettingItem(
                        title = stringResource(R.string.speech_analysis_mode),
                        selectedValue = state.speechAnalysisMode,
                        displayEntries = arrayOf(
                            stringResource(R.string.speech_analysis_rule),
                            stringResource(R.string.speech_analysis_rule_ai),
                            stringResource(R.string.speech_analysis_ai),
                        ),
                        entryValues = arrayOf("rule", "rule_with_ai", "ai_understanding"),
                        description = when (state.speechAnalysisMode) {
                            "rule_with_ai" -> stringResource(R.string.speech_analysis_rule_ai_summary)
                            "ai_understanding" -> stringResource(R.string.speech_analysis_ai_summary)
                            else -> stringResource(R.string.speech_analysis_rule_summary)
                        },
                        onValueChange = { onIntent(ReadBookIntent.SetSpeechAnalysisMode(it)) },
                    )
                    TinySwitchSettingItem(
                        title = stringResource(R.string.use_multi_speaker),
                        description = stringResource(R.string.use_multi_speaker_summary),
                        checked = state.useMultiSpeaker,
                        onCheckedChange = {
                            onIntent(ReadBookIntent.SetUseMultiSpeaker(it))
                        },
                    )
                    TinyClickableSettingItem(
                        title = stringResource(R.string.sys_tts_config),
                        onClick = { onIntent(ReadBookIntent.OpenSystemTtsSettings) },
                    )
                    SliderSettingItem(
                        title = stringResource(R.string.read_aloud_preload),
                        description = stringResource(
                            R.string.read_aloud_preload_summary,
                            state.preDownloadNum,
                        ),
                        value = state.preDownloadNum.toFloat(),
                        defaultValue = 10f,
                        valueRange = 0f..100f,
                        onValueChange = {
                            onIntent(ReadBookIntent.ApplyPreDownloadNum(it.toInt()))
                        },
                    )
                    TinySwitchSettingItem(
                        title = "段落上下文（MiMo）",
                        description = "上下文只用于理解语气，不加入朗读正文。重新开始听书后生效。",
                        checked = state.ttsContextEnabled,
                        onCheckedChange = { onIntent(ReadBookIntent.SetTtsContextEnabled(it)) },
                    )
                    SliderSettingItem(
                        title = "提前缓冲页数",
                        description = "提前准备 ${state.ttsBufferPages} 页（每页按 600 字估算），当前章准备好后才预缓存后续章节。",
                        value = state.ttsBufferPages.toFloat(), defaultValue = 2f, valueRange = 1f..20f,
                        onValueChange = { onIntent(ReadBookIntent.SetTtsBufferPages(it.toInt())) },
                    )
                    SliderSettingItem(
                        title = "单次合成字数上限",
                        description = "${state.ttsChunkChars} 字；角色切换处仍会分段。重新开始听书后生效。",
                        value = state.ttsChunkChars.toFloat(), defaultValue = 600f, valueRange = 180f..1800f,
                        onValueChange = { onIntent(ReadBookIntent.SetTtsChunkChars(it.toInt())) },
                    )
                    SliderSettingItem(
                        title = stringResource(R.string.tts_pre_synthesis_concurrency),
                        description = stringResource(
                            R.string.tts_pre_synthesis_concurrency_summary,
                            state.preSynthesisConcurrency,
                        ),
                        value = state.preSynthesisConcurrency.toFloat(),
                        defaultValue = 3f,
                        valueRange = 1f..8f,
                        onValueChange = {
                            onIntent(ReadBookIntent.ApplyPreSynthesisConcurrency(it.toInt()))
                        },
                    )
                    SliderSettingItem(
                        title = stringResource(R.string.tts_paragraph_interval),
                        description = stringResource(
                            R.string.tts_paragraph_interval_summary,
                            state.readAloudParagraphInterval,
                        ),
                        value = state.readAloudParagraphInterval.toFloat(),
                        defaultValue = 100f,
                        valueRange = 0f..5000f,
                        onValueChange = {
                            onIntent(ReadBookIntent.ApplyParagraphInterval(it.toInt()))
                        },
                    )
                    SliderSettingItem(
                        title = stringResource(R.string.audio_cache_clean_time),
                        description = stringResource(
                            R.string.audio_cache_clean_time_summary,
                            state.audioCacheCleanTime,
                        ),
                        value = state.audioCacheCleanTime.toFloat(),
                        defaultValue = 10f,
                        valueRange = 0f..10080f,
                        onValueChange = {
                            onIntent(ReadBookIntent.ApplyAudioCacheCleanTime(it.toInt()))
                        },
                    )
                    TinyClickableSettingItem(
                        title = stringResource(R.string.clear_cache),
                        onClick = { onIntent(ReadBookIntent.ClearTtsCache) },
                    )
                }
            }
        }
    }
}
