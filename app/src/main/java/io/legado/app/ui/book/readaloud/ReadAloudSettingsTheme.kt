package io.legado.app.ui.book.readaloud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.legado.app.domain.gateway.ReadSettingsGateway
import io.legado.app.domain.gateway.ReadStyleGateway
import io.legado.app.ui.book.read.rememberReadBookColorTheme
import io.legado.app.ui.theme.LegadoTheme
import io.legado.app.ui.theme.ProvideThemeOverride
import org.koin.compose.koinInject

@Composable
fun ReadAloudSettingsTheme(content: @Composable () -> Unit) {
    val readSettings = koinInject<ReadSettingsGateway>()
    val readStyle = koinInject<ReadStyleGateway>()
    val preferences by readSettings.settings.collectAsStateWithLifecycle(
        initialValue = readSettings.currentSettings,
    )
    val styleState by readStyle.state.collectAsStateWithLifecycle()
    ProvideThemeOverride(
        theme = rememberReadBookColorTheme(
            styleKey = styleState,
            preferences = preferences,
            isAppDark = LegadoTheme.isDark,
        ),
        content = content,
    )
}
